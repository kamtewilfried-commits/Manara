const { StreamChat } = require('stream-chat');

const apiKey = 'wrejdr2mfku7';
const apiSecret = process.env.STREAM_API_SECRET;
const userId = 'demo-user-F671Z57u';

const serverClient = StreamChat.getInstance(apiKey, apiSecret);
const token = serverClient.createToken(userId);

console.log(token);