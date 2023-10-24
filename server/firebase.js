import { initializeApp } from 'firebase/app';
import { child, get, getDatabase, ref, set } from 'firebase/database';

const firebaseApp = initializeApp({
    apiKey: process.env.FB_API_KEY,
    authDomain: process.env.FB_AUTH_DOMAIN,
    databaseURL: process.env.FB_DATABASE_URL,
    projectId: process.env.FB_PROJECT_ID,
    storageBucket: process.env.FB_STORAGE_BUCKET,
    messagingSenderId: process.env.FB_MESSAGING_SENDER_ID,
    appId: process.env.FB_APP_ID,
    measurementId: process.env.FB_MEASUREMENT_ID
});
const database = getDatabase(firebaseApp);

export const setData = (path, value) => set(ref(database, path), value);

export const getData = (path) => get(child(ref(database), path)).then((snapshot) => snapshot.val());

export default database;