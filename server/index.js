import 'dotenv/config';
import { createServer } from 'http';
import { Server } from 'socket.io';
import express from 'express';
import ids from './ids.js';
import { getData, setData } from './firebase.js';

class CustomError extends Error {
    constructor(message) {
        super(message);
        this.name = 'CustomError';
    }
}

const {
    // PROJECT
    PORT = 3000,
    // OPENAPI CHATGPT
    OPENAPI_KEY
} = process.env;

const app = express();
const server = createServer(app);
const io = new Server(server);

const questionTypes = ['TRUE_OR_FALSE', 'MULTIPLE_CHOICE', 'IDENTIFICATION'];
const interval = 1000 * 60 * 1; // 1 minute
const queue = [];

const processRequests = async () => {
    if (queue.length === 0) return;
    console.log('Queue length:', queue.length);

    const request = queue.pop();
    const { userId, topicId, content, items, count } = request;

    console.log('Request:', userId, topicId, items, count);

    if (count > 5) return io.emit('error', userId, 'Request reached max retries');

    try {
        const userExisting = await getData(`users/${userId}`);
        if (!userExisting) throw new CustomError('User not existing');

        const topicExisting = await getData(`users/${userId}/topics/${topicId}`);
        if (!topicExisting) throw new CustomError('Topic not existing');

        let results = await Promise.all(
            createPrompt(content, items).map((prompt) =>
                fetch(`https://api.openai.com/v1/chat/completions`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${OPENAPI_KEY}`
                    },
                    body: JSON.stringify({
                        model: 'gpt-3.5-turbo-16k',
                        messages: [
                            {
                                role: 'user',
                                content: prompt
                            }
                        ]
                    })
                })
            )
        );
        
        if (results.some((r) => !r.ok)) throw new Error('Not all requests were successful');
        results = await Promise.all(results.map((r) => r.json()));

        results = results
            .map(
                ({
                    choices: [
                        {
                            message: { content }
                        }
                    ]
                }) => {
                    try {
                        return JSON.parse(content);
                    } catch (e) {
                        throw content;
                    }
                }
            )
            .flatMap((parsed, i) => parsed.map((question) => ({ ...question, type: questionTypes[i] })))
            .map(({ question, answer, options, type }) => {
                const qn = {
                    questionId: ids(),
                    question,
                    answer: answer.toString(),
                    type
                };

                if (options instanceof Array) {
                    qn.choices = options;
                    qn.answer = options[answer];
                }
                if (options instanceof Array) {
                    qn.choices = options;
                    qn.answer = options[answer];
                }

                return qn;
            })
            .reduce((all, question) => ({ ...all, [question.questionId]: question }), {});

        const quizId = ids();
        await setData(`users/${userId}/topics/${topicId}/quizzes/${quizId}`, {
            average: 0,
            retries: 0,
            quizId: quizId,
            itemsPerLevel: items,
            questions: results
        });

        io.emit('chatgpt', userId);
    } catch (e) {
        console.log(e.message || e);

        if (e instanceof CustomError) return io.emit('error', userId, e.message);

        queue.unshift({ ...request, count: count + 1 });
    }
};

setInterval(processRequests, interval);

io.on('connection', (socket) => {
    console.log(`New client: ${socket.id}`);

    socket.on('disconnect', () => {
        console.log(`User disconnected: ${socket.id}`);
    });

    socket.on('chatgpt', (data) => {
        const { userId, topicId, content, items } = data;
        /* prettier-ignore */
        const isValid = typeof userId === 'string' && userId.length > 0 &&
                        typeof topicId === 'string' && topicId.length === 16 &&
                        typeof content === 'string' && content.length > 0 &&
                        typeof items === 'number' && items > 0 && items <= 10 && items % 5 === 0;

        if (!isValid) return;

        if (queue.some((req) => req.userId === userId && req.topicId === topicId))
            return io.emit('error', userId, 'Request already in queue');

        queue.unshift({ ...data, count: 0 });
    });
});

server.listen(PORT, () => console.log(`Listening on port ${PORT}`));

function createPrompt(content, items) {
    return [
        {
            type: 'true or false',
            format: '[{ question, answer }]'
        },
        {
            type: 'multiple choice',
            format: '[{ question, options: [<no numbering>], answer: <0-3> }]'
        },
        {
            type: 'fill in the blanks',
            format: '[{ question: <statement, answer replaced by udnerlines>, answer: <only 1 to 3 words> }]'
        }
    ].map(
        ({ type, format }) => `With this given content:
"${content}"
"${content}"

Write me a ${items} ${type} questions, written in this json format: ${format}`
    );
}
