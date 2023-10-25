import 'dotenv/config';
import { ChatGPTAPI } from 'chatgpt';
import { createServer } from 'http';
import { Server } from 'socket.io';
import express from 'express';
import ids from './ids.js';
import { getData, setData } from './firebase.js';

const {
    // PROJECT
    PORT,
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
        max_tokens: 4096
    }
});
const chatgptPromptQueue = [];
const intervalTime = 1000 * 60; // 1 minute
const sessions = new Map();
const questionTypes = ['TRUE_OR_FALSE', 'MULTIPLE_CHOICE', 'IDENTIFICATION'];

// Set up an interval to periodically execute the code block
setInterval(() => {
    // Check if there are any items in the chatgptPromptQueue, if not, return
    if (chatgptPromptQueue.length == 0) return;

    // Get the last item from the chatgptPromptQueue
    const quizRequest = chatgptPromptQueue.pop();
    const { userId, topicId, content, items } = quizRequest;

    // Get user data by making a GET request to the server
    getData(`users/${userId}`)
        .then((user) => {
            // If the user does not exist, throw an error
            if (user === null) throw 1;
            return Promise.allSettled(createPrompt(content, items).map((prompt) => chatgpt.sendMessage(prompt)));
            // Get topic data by making a GET request to the server
        })
        .then((results) => {
            if (results.some((result) => result.status === 'rejected')) throw 2;

            const questions = results
                .map((result, idx) =>
                    JSON.parse(result.value.text).map((parsed) => ({ ...parsed, type: questionTypes[idx] }))
                )
                .flat()
                .map(({ question, answer, options, type }, idx) => {
                    const qn = { questionId: ids(), question, answer, type };

            // If the topic does not exist, throw an error
            // Create prompts from the quiz content and items
            // Send each prompt to the chatgpt server and get the response
            // Parse the response from chatgpt server and transform it into an array of questions
                    // Create a question object with questionId, question, answer and type
                    // If options is an array, add choices and set the answer to the option value
                    if (options instanceof Array) {
                        qn.choices = options;
                        qn.answer = options[answer];
                    }

                    return qn;
                })
                .reduce((all, question) => ({ ...all, [question.questionId]: question }), {});

                // Convert the array of questions into an object with questionId as key
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
        .then(() => sessions.get(userId).forEach((socketId) => io.to(socketId).emit('chatgpt', true)))
        .catch((e) => {
            // If the error is 1, return
            if (e === 1) return;
            // Add the quizRequest back to the beginning of the chatgptPromptQueue
            chatgptPromptQueue.unshift(quizRequest);
        });
}, intervalTime);

io.on('connection', (socket) => {
    socket.on('disconnect', () => {
        for (const userId of sessions.keys()) {
            const set = sessions.get(userId);

            set.delete(socket.id);
            if (set.size == 0) sessions.delete(userId);
        }
    });

    socket.on('chatgpt', (data) => {
        const { userId } = data;

        if (sessions.has(userId)) sessions.get(userId).add(socket.id);
        else sessions.set(userId, new Set(socket.id));
        /* prettier-ignore */

        chatgptPromptQueue.unshift(data);
    });
});

server.listen(PORT || 3000, () => console.log('Server started'));

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
${content}

Write me a ${items} ${type} questions, written in this json format: ${format}`
    );
}
