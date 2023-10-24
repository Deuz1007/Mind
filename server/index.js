import 'dotenv/config';
import { ChatGPTAPI } from 'chatgpt';
import { createServer } from 'http';
import { child, get, getDatabase, ref, set } from 'firebase/database';
import { initializeApp } from 'firebase/app';
import { Server } from 'socket.io';
import express from 'express';
import ids from './ids.js';

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
        temperature: 0
    }
});
const chatgptPromptQueue = [
    {
        userId: 'asuakjdfhkajsdfh',
        topicId: 'kfgbWkYz2ozXH09i',
        content:
            'Cells are the basic building blocks of all living things. The human body is composed of trillions of cells. They provide structure for the body, take in nutrients from food, convert those nutrients into energy, and carry out specialized functions. Cells also contain the body’s hereditary material and can make copies of themselves.Cells have many parts, each with a different function. Some of these parts, called organelles, are specialized structures that perform certain tasks within the cell. Human cells contain the following major parts, listed in alphabetical order:CytoplasmWithin cells, the cytoplasm is made up of a jelly-like fluid (called the cytosol) and other structures that surround the nucleus.CytoskeletonThe cytoskeleton is a network of long fibers that make up the cell’s structural framework. The cytoskeleton has several critical functions, including determining cell shape, participating in cell division, and allowing cells to move. It also provides a track-like system that directs the movement of organelles and other substances within cells.Endoplasmic reticulum (ER)This organelle helps process molecules created by the cell. The endoplasmic reticulum also transports these molecules to their specific destinations either inside or outside the cell.Golgi apparatusThe Golgi apparatus packages molecules processed by the endoplasmic reticulum to be transported out of the cell.Lysosomes and peroxisomesThese organelles are the recycling center of the cell. They digest foreign bacteria that invade the cell, rid the cell of toxic substances, and recycle worn-out cell components.MitochondriaMitochondria are complex organelles that convert energy from food into a form that the cell can use. They have their own genetic material, separate from the DNA in the nucleus, and can make copies of themselves.NucleusThe nucleus serves as the cell’s command center, sending directions to the cell to grow, mature, divide, or die. It also houses DNA (deoxyribonucleic acid), the cell’s hereditary material. The nucleus is surrounded by a membrane called the nuclear envelope, which protects the DNA and separates the nucleus from the rest of the cell.Plasma membraneThe plasma membrane is the outer lining of the cell. It separates the cell from its environment and allows materials to enter and leave the cell.RibosomesRibosomes are organelles that process the cell’s genetic instructions to create proteins. These organelles can float freely in the cytoplasm or be connected to the endoplasmic reticulum (see above).',
        items: 5
    }
];
const intervalTime = 1000 * 60; // 1 minute

// const firebaseApp = initializeApp({
//     apiKey: FB_API_KEY,
//     authDomain: FB_AUTH_DOMAIN,
//     databaseURL: FB_DATABASE_URL,
//     projectId: FB_PROJECT_ID,
//     storageBucket: FB_STORAGE_BUCKET,
//     messagingSenderId: FB_MESSAGING_SENDER_ID,
//     appId: FB_APP_ID,
//     measurementId: FB_MEASUREMENT_ID
// });
// const database = getDatabase(firebaseApp);

import database, { getData, setData } from './firebase.js';

const sessions = new Map();

setInterval(() => {
    if (chatgptPromptQueue.length == 0) return;

    const quizRequest = chatgptPromptQueue.pop();
    const { userId, topicId, content, items } = quizRequest;

    // ref(database)
    // get(child(ref(database), `users/${userId}`)).then((snapshot) => {
    //     console.log(snapshot.val());
    // })

    // Check given userId is existingin database
    getData(`users/${userId}`)
        .then((user) => {
            if (user === null) throw new Error('User not found');
            return Promise.allSettled(createPrompt(content, items).map((prompt) => chatgpt.sendMessage(prompt)));
        })
        .then((results) => {
            if (results.some((result) => result.status === 'rejected')) {
                // Re-add to queue
                chatgptPromptQueue.unshift(quizRequest);
                return;
            }

            const level1 = JSON.parse(results[0].value.text); // true or false
            const level2 = JSON.parse(results[1].value.text); // multiple choice
            const level3 = JSON.parse(results[2].value.text); // fill in the blanks

            const questions = [
                ...level1.map(({ question, answer }) => ({
                    questionId: ids(),
                    question,
                    answer,
                    type: 'TRUE_OR_FALSE'
                })),
                ...level2.map(({ question, answer, options: choices }) => ({
                    questionId: ids(),
                    question,
                    answer: choices[answer],
                    choices,
                    type: 'MULTIPLE_CHOICE'
                })),
                ...level3.map(({ question, answer }) => ({
                    questionId: ids(),
                    question,
                    answer,
                    type: 'IDENTIFICATION'
                }))
            ].reduce((all, question) => ({ ...all, [question.questionId]: question }), {});

            // Save to firebase
            return set(ref(database, `users/${userId}/topics/${topicId}/quizzes/${ids()}`), {
                average: 0,
                retries: 0,
                quizId: ids(),
                itemsPerLevel: items,
                questions
            });
        })
        .then(() => {
            // Emit to application
            sessions.get(userId).forEach((socketId) => io.to(socketId).emit('chatgpt', true));
        })
        .catch(console.log);

    // database.ref(`users/${userId}`).on('value').then((snapshot) => {
    //     console.log(snapshot.val());
    // })

    Promise.allSettled(createPrompt(content, items).map((prompt) => chatgpt.sendMessage(prompt)))
        .then((results) => {
            if (results.some((result) => result.status === 'rejected')) {
                // Re-add to queue
                chatgptPromptQueue.unshift(quizRequest);
                return;
            }

            const level1 = JSON.parse(results[0].value.text); // true or false
            const level2 = JSON.parse(results[1].value.text); // multiple choice
            const level3 = JSON.parse(results[2].value.text); // fill in the blanks

            const questions = [
                ...level1.map(({ question, answer }) => ({
                    questionId: ids(),
                    question,
                    answer,
                    type: 'TRUE_OR_FALSE'
                })),
                ...level2.map(({ question, answer, options: choices }) => ({
                    questionId: ids(),
                    question,
                    answer: choices[answer],
                    choices,
                    type: 'MULTIPLE_CHOICE'
                })),
                ...level3.map(({ question, answer }) => ({
                    questionId: ids(),
                    question,
                    answer,
                    type: 'IDENTIFICATION'
                }))
            ].reduce((all, question) => ({ ...all, [question.questionId]: question }), {});

            // Save to firebase
            return set(ref(database, `users/${userId}/topics/${topicId}/quizzes/${ids()}`), {
                average: 0,
                retries: 0,
                quizId: ids(),
                itemsPerLevel: items,
                questions
            });
        })
        .then(() => {
            // Emit to application
            sessions.get(userId).forEach((socketId) => io.to(socketId).emit('chatgpt', true));
        })
        .catch(() => chatgptPromptQueue.unshift(quizRequest));
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
