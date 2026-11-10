'use strict';

let stompClient = null;
//let username = null;

let currentMode = 'GROUP'; // GROUP | PRIVATE | PUBLIC
let currentGroup = 'general';
let currentPrivateRecipient = null;

const groupSubscriptions = {};

const connectedUserLabel = document.getElementById('connectedUserLabel');
const activeRoomName = document.getElementById('activeRoomName');
const messagesWindow = document.getElementById('messagesWindow');
const messageInput = document.getElementById('messageInput');
const sendButton = document.getElementById('sendButton');
const mediaInput = document.getElementById('mediaInput');

function addMessage(text, mine = false) {
    const row = document.createElement('div');
    row.classList.add('chat-message-row');
    if (mine) row.classList.add('me');

    const bubble = document.createElement('div');
    bubble.classList.add('chat-bubble');
    bubble.textContent = text;

    row.appendChild(bubble);
    messagesWindow.appendChild(row);
    messagesWindow.scrollTop = messagesWindow.scrollHeight;
}

function addSystemMessage(text) {
    const info = document.createElement('div');
    info.classList.add('chat-system');
    info.textContent = text;
    messagesWindow.appendChild(info);
    messagesWindow.scrollTop = messagesWindow.scrollHeight;
}

function connect() {
   /* username = document.getElementById('name').value.trim();

    if (!username) {
        addSystemMessage("Entre un nom d'utilisateur.");
        return;
    }*/

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    //connectedUserLabel.textContent = username;

    // Public
    stompClient.subscribe('/topic/public', onPublicMessageReceived);

    // Privé
    stompClient.subscribe('/user/queue/messages', onPrivateMessageReceived);

    // Groupe par défaut
    joinGroup('general', 'Général');

    stompClient.send('/app/chat.addUser', {}, JSON.stringify({
        //sender: username,
        type: 'JOIN'
    }));

   //addSystemMessage('Connexion au serveur établie.');
    addSystemMessage('Connecté au chat.');

    // appel backend pour récupérer email
    fetch("/api/user")
        .then(res => res.json())
        .then(user => {
            document.getElementById('connectedUserLabel').textContent = user.email;
        })
        .catch(() => {
            document.getElementById('connectedUserLabel').textContent = "Utilisateur";
        });
}

function onError(error) {
    console.error(error);
    addSystemMessage('Échec de la connexion WebSocket.');
}

function onPublicMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    if (message.type === 'JOIN') {
        addSystemMessage(message.sender + ' a rejoint le chat.');
    } else if (message.type === 'LEAVE') {
        addSystemMessage(message.sender + ' a quitté le chat.');
    } else {
        addMessage('[PUBLIC] ' + message.sender + ' : ' + message.content, false);
    }
}

function onPrivateMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    addMessage('[PRIVÉ] ' + message.sender + ' : ' + message.content, false);
}

function onGroupMessageReceived(groupName, payload) {
    const message = JSON.parse(payload.body);
    addMessage('[' + groupName + '] ' + message.sender + ' : ' + message.content, false);
}

function joinGroup(groupName, label) {
    if (!stompClient) {
        addSystemMessage("Connecte-toi d'abord.");
        return;
    }

    currentMode = 'GROUP';
    currentGroup = groupName;
    currentPrivateRecipient = null;
    activeRoomName.textContent = label || groupName;

    if (!groupSubscriptions[groupName]) {
        groupSubscriptions[groupName] = stompClient.subscribe('/topic/group/' + groupName, function(payload) {
            onGroupMessageReceived(groupName, payload);
        });
    }

    addSystemMessage('Vous avez rejoint le salon : ' + (label || groupName));
}

function openPrivateChat(recipient) {
    recipient = recipient.trim();

    if (!recipient) {
        addSystemMessage("Entre un utilisateur pour le chat privé.");
        return;
    }

    currentMode = 'PRIVATE';
    currentPrivateRecipient = recipient;
    activeRoomName.textContent = 'Privé : ' + recipient;
    addSystemMessage('Conversation privée ouverte avec ' + recipient);
}

function sendCurrentMessage() {
    const text = messageInput.value.trim();

    if (!text) return;
    if (!stompClient) {
        addSystemMessage("Connecte-toi d'abord.");
        return;
    }

    if (currentMode === 'PUBLIC') {
        stompClient.send('/app/chat.sendPublic', {}, JSON.stringify({
           // sender: username,
            content: text,
            type: 'PUBLIC'
        }));
        addMessage('Moi : ' + text, true);
    } else if (currentMode === 'PRIVATE') {
        if (!currentPrivateRecipient) {
            addSystemMessage('Aucun destinataire privé sélectionné.');
            return;
        }

        stompClient.send('/app/chat.sendPrivate', {}, JSON.stringify({
           //sender: username,
            recipient: currentPrivateRecipient,
            content: text,
            type: 'PRIVATE'
        }));
        addMessage('Moi → ' + currentPrivateRecipient + ' : ' + text, true);
    } else {
        if (!currentGroup) {
            addSystemMessage('Aucun salon sélectionné.');
            return;
        }

        stompClient.send('/app/chat.sendGroup', {}, JSON.stringify({
           // sender: username,
            groupId: currentGroup,
            content: text,
            type: 'GROUP'
        }));
        addMessage('Moi : ' + text, true);
    }

    messageInput.value = '';
    messageInput.focus();
}

function createDynamicGroup() {
    const name = document.getElementById('newGroupName').value.trim();
    if (!name) {
        addSystemMessage('Entre un nom de groupe.');
        return;
    }

    // Ajout visuel facultatif dans la sidebar
    const rooms = document.getElementById('chatRooms');
    const existing = rooms.querySelector('[data-group="' + name + '"]');

    if (!existing) {
        const div = document.createElement('div');
        div.className = 'chat-user-pill mt-2';
        div.setAttribute('data-group', name);
        div.setAttribute('data-label', name);
        div.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <span>${name}</span>
                <span class="badge badge-info">Groupe</span>
            </div>
            <div class="small text-secondary">Salon dynamique.</div>
        `;
        rooms.appendChild(div);

        div.addEventListener('click', function() {
            document.querySelectorAll('.chat-user-pill').forEach(p => p.classList.remove('active'));
            div.classList.add('active');
            joinGroup(name, name);
        });
    }

    document.querySelectorAll('.chat-user-pill').forEach(p => p.classList.remove('active'));
    const newPill = rooms.querySelector('[data-group="' + name + '"]');
    if (newPill) newPill.classList.add('active');

    joinGroup(name, name);
    document.getElementById('newGroupName').value = '';
}

function addMediaBubble(url, mine = false) {
    const row = document.createElement('div');
    row.classList.add('chat-message-row');
    if (mine) row.classList.add('me');

    const bubble = document.createElement('div');
    bubble.classList.add('chat-bubble');

    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.textContent = 'Fichier envoyé';
    bubble.appendChild(link);

    row.appendChild(bubble);
    messagesWindow.appendChild(row);
    messagesWindow.scrollTop = messagesWindow.scrollHeight;
}
window.addEventListener('load', function () {
    connect();
});

document.getElementById('btn-create-group').addEventListener('click', createDynamicGroup);

document.getElementById('btn-open-private').addEventListener('click', function() {
    openPrivateChat(document.getElementById('privateRecipient').value);
});

document.querySelectorAll('.chat-user-pill').forEach(pill => {
    pill.addEventListener('click', function() {
        const group = pill.getAttribute('data-group');
        const label = pill.getAttribute('data-label') || group;

        document.querySelectorAll('.chat-user-pill').forEach(p => p.classList.remove('active'));
        pill.classList.add('active');

        joinGroup(group, label);
    });
});

sendButton.addEventListener('click', sendCurrentMessage);

messageInput.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        sendCurrentMessage();
    }
});

mediaInput.addEventListener('change', function() {
    const file = mediaInput.files[0];
    if (!file) return;

    // Placeholder visuel seulement pour l'instant
    addSystemMessage('Upload non branché au backend pour le moment : ' + file.name);
    mediaInput.value = '';
});