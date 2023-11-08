import 'dotenv/config';
import { ChatGPTAPI } from 'chatgpt';
import { createServer } from 'http';
import { Server } from 'socket.io';
import express from 'express';
import ids from './ids.js';
import { getData, setData } from './firebase.js';

const {
    // PROJECT
    PORT = 3000,
    // OPENAPI CHATGPT
    OPENAPI_KEY
} = process.env;

const app = express();
const server = createServer(app);
const io = new Server(server);

const chatgpt = new ChatGPTAPI({
    apiKey: OPENAPI_KEY,
    completionParams: {
        model: 'gpt-3.5-turbo-16k',
        temperature: 0,
        max_tokens: 8192
    }
});
const chatgptPromptQueue = [];
const intervalTime = 1000 * 60 * 2; // 2 minutes
const questionTypes = ['TRUE_OR_FALSE', 'MULTIPLE_CHOICE', 'IDENTIFICATION'];

// Set up an interval to periodically execute the code block
setInterval(() => {
    // Check if there are any items in the chatgptPromptQueue, if not, return
    if (chatgptPromptQueue.length == 0) return;

    // Get the last item from the chatgptPromptQueue
    const quizRequest = chatgptPromptQueue.pop();
    const { userId, topicId, content, items, count } = quizRequest;

    if (count >= 5) return;

    // Get user data by making a GET request to the server
    getData(`users/${userId}`)
        .then((user) => {
            // If the user does not exist, throw an error
            if (user === null) throw 1;
            // Get topic data by making a GET request to the server
            return getData(`users/${userId}/topics/${topicId}`);
        })
        .then((topic) => {
            // If the topic does not exist, throw an error
            if (topic === null) throw 1;
            // Create prompts from the quiz content and items
            const prompts = createPrompt(content, items);
            // Send each prompt to the chatgpt server and get the response
            return Promise.all(prompts.map((prompt) => chatgpt.sendMessage(prompt)));
        })
        .then((results) =>
            // Parse the response from chatgpt server and transform it into an array of questions
            results
                .map(({ text }) => {
                    try {
                        return JSON.parse(text);
                    } catch (e) {
                        throw text;
                    }
                })
                .flatMap((parsed, i) => parsed.map((question) => ({ ...question, type: questionTypes[i] })))
                .map(({ question, answer, options, type }) => {
                    // Create a question object with questionId, question, answer and type
                    const qn = {
                        questionId: ids(),
                        question,
                        answer: answer.toString(),
                        type
                    };

                    // If options is an array, add choices and set the answer to the option value
                    if (options instanceof Array) {
                        qn.choices = options;
                        qn.answer = options[answer];
                    }

                    return qn;
                })
                // Convert the array of questions into an object with questionId as key
                .reduce((all, question) => ({ ...all, [question.questionId]: question }), {})
        )
        .then((questions) => {
            // Generate a quizId and save the quiz data to the server
            const quizId = ids();
            return setData(`users/${userId}/topics/${topicId}/quizzes/${quizId}`, {
                average: 0,
                retries: 0,
                quizId: quizId,
                itemsPerLevel: items,
                questions
            });
        })
        .then(() => io.emit('chatgpt', userId))
        .catch((e) => {
            console.log(e);

            // If the error is 1, return
            if (e === 1) return;
            if (typeof e === 'string') return io.emit('error', userId, e);

            // Add the quizRequest back to the beginning of the chatgptPromptQueue
            chatgptPromptQueue.unshift({ ...quizRequest, count: count + 1 });
        });
}, intervalTime);

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
                        typeof items === 'number' && items > 0 && items <= 15 && items % 5 === 0;

        if (!isValid) return;

        chatgptPromptQueue.unshift({ ...data, count: 0 });
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

Write me a ${items} ${type} questions, written in this json format: ${format}`
    );
}
