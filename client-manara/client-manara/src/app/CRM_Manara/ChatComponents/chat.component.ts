import { AfterViewInit, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from 'stream-chat';


import {
  ChatClientService,
  ChannelService,
  StreamI18nService,
  CustomTemplatesService,
  MessageContext,
  ThemeService
} from 'stream-chat-angular';
interface ChatUser {
  id: string;
  name: string;
  email: string;
}
interface StreamTokenResponse {
  token: string;
  userId: string;
  name: string;
  email?: string;
}

@Component({
  selector: 'app-chat-root',
  standalone: false,
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
})
export class ChatComponent implements OnInit, AfterViewInit {

  groupMode = false;
selectedGroupUsers: ChatUser[] = [];
groupName = '';
 
  isCalling = false;
users: ChatUser[] = [];
filteredUsers: ChatUser[] = [];
searchTerm = '';
selectedUser?: ChatUser;



groupChannels: any[] = [];

loadGroupChannels(): void {
  this.chatService.chatClient.queryChannels({
    type: 'messaging',
    members: { $in: [this.currentUserId] }
  }).then(channels => {
    this.groupChannels = channels.filter(channel =>
      Object.keys(channel.state.members).length > 2
    );

    console.log('GROUPES TROUVÉS:', this.groupChannels);
  }).catch(err => {
    console.error('Erreur chargement groupes:', err);
  });
}

async openGroupChannel(channel: any): Promise<void> {
  await channel.watch();
 if (channel.id) {
    this.unreadByChannel[channel.id] = 0;
  }

this.incomingCallChannelId = null;
  this.selectedUser = undefined;
  this.activeChannel = channel;
  
  this.channelService.setAsActiveChannel(channel);
}

  readonly apiKey = 'wrejdr2mfku7';

currentUserId = '';
currentUserName = '';



@ViewChild('customMessageTemplate')
messageTemplate!: TemplateRef<MessageContext>;

activeChannel: any = null;
unreadByChannel: { [channelId: string]: number } = {};
unreadByUser: { [userId: string]: number } = {};

incomingCallChannelId: string | null = null;
incomingCallUserId: string | null = null;
isSelectedForGroup(user: ChatUser): boolean {
  return this.selectedGroupUsers.some(u => u.id === user.id);
}

  constructor(
    private http: HttpClient,
    private chatService: ChatClientService,
    private channelService: ChannelService,
    private streamI18nService: StreamI18nService,
    private themeService: ThemeService,
    private customTemplatesService: CustomTemplatesService,
  ) {
    this.themeService.theme$.next('dark');
    this.streamI18nService.setTranslation();
  }

  ngOnInit(): void {
    this.initChat();
  }

  private initChat(): void {
  this.http.get<StreamTokenResponse>(
    'http://localhost:8080/api/chatting/token',
    { withCredentials: true }
  ).subscribe({
    next: async (res) => {
      this.currentUserId = res.userId;
      this.currentUserName = res.name || res.userId;

      const user: User = {
        id: this.currentUserId,
        name: this.currentUserName,
        image: `https://getstream.io/random_png/?name=${encodeURIComponent(this.currentUserName)}`,
      };

      await this.chatService.init(this.apiKey, user, res.token);

      this.loadUsers();
      this.loadGroupChannels();
      this.listenForMessages();
      
    },
    error: (err) => {
      console.error('Erreur récupération token Stream:', err);
    }
  });
}

  ngAfterViewInit(): void {
    this.customTemplatesService.messageTemplate$.next(this.messageTemplate);
  }

  loadUsers(): void {
  this.http.get<ChatUser[]>(
    'http://localhost:8080/api/chatting/users',
    { withCredentials: true }
  ).subscribe({
    next: (users) => {
      this.users = users;
      this.filteredUsers = users;
    },
    error: (err) => {
      console.error('Erreur chargement utilisateurs:', err);
    }
  });
}
searchUsers(): void {
  const term = this.searchTerm.toLowerCase().trim();

  this.filteredUsers = this.users.filter(user =>
    user.name.toLowerCase().includes(term) ||
    user.email.toLowerCase().includes(term)
  );
}
async openConversation(otherUser: ChatUser): Promise<void> {
  if (!this.currentUserId) {
    console.error('Utilisateur courant non initialisé');
    return;
  }

  this.selectedUser = otherUser;

  const channelId = [this.currentUserId, otherUser.id]
    .sort()
    .join('-');

  const channel = this.chatService.chatClient.channel(
    'messaging',
    channelId,
    {
      name: `${this.currentUserName} - ${otherUser.name}`,
      members: [this.currentUserId, otherUser.id],
    }
  );

  await channel.watch();

  this.channelService.setAsActiveChannel(channel);
  this.activeChannel = channel;
 const currentChannelId = channel.id;

if (currentChannelId) {
  this.unreadByChannel[currentChannelId] = 0;
}
this.unreadByUser[otherUser.id] = 0;

this.incomingCallUserId = null;
this.incomingCallChannelId = null;

  console.log('Conversation ouverte avec:', otherUser.name);
  console.log('Channel ID:', channelId);
}
toggleUserForGroup(user: ChatUser): void {
  const exists = this.selectedGroupUsers.some(u => u.id === user.id);

  if (exists) {
    this.selectedGroupUsers = this.selectedGroupUsers.filter(u => u.id !== user.id);
  } else {
    this.selectedGroupUsers.push(user);
  }
}
async createGroupConversation(): Promise<void> {
  if (!this.currentUserId) {
    console.error('Utilisateur courant non initialisé');
    return;
  }

  if (this.selectedGroupUsers.length < 2) {
    console.error('Sélectionne au moins 2 utilisateurs pour créer un groupe');
    return;
  }

  const memberIds = Array.from(new Set([
    this.currentUserId,
    ...this.selectedGroupUsers.map(u => u.id)
  ]));

 const channelId = `group-${Date.now()}`;

const channelData = {
  name: this.groupName.trim() || 'Nouveau groupe',
  members: memberIds,
  isGroup: true,
 createdByUserId: this.currentUserId
} as any;

const channel = this.chatService.chatClient.channel(
  'messaging',
  channelId,
  channelData
);
  await channel.watch();

  this.channelService.setAsActiveChannel(channel);
  this.activeChannel = channel;
  this.groupChannels = [
  channel,
  ...this.groupChannels.filter(g => g.id !== channel.id)
];

  this.loadGroupChannels();

  this.selectedUser = undefined;
  this.selectedGroupUsers = [];
  this.groupName = '';
  this.groupMode = false;

  console.log('Groupe créé:', channelId);
}

async deleteGroupChannel(channel: any, event?: Event): Promise<void> {
  event?.stopPropagation();

  if (!channel) return;

  const ok = confirm(`Supprimer le groupe "${channel.data?.name || 'Sans nom'}" ?`);
  if (!ok) return;

  try {
    await channel.watch();

    // suppression Stream
    await channel.delete();

    // retirer de la liste
    this.groupChannels = this.groupChannels.filter(g => g.id !== channel.id);

    // si le groupe supprimé est ouvert
    if (this.activeChannel?.id === channel.id) {
      this.activeChannel = null;
    }

    console.log('Groupe supprimé');
  } catch (err: any) {
    console.error('Erreur suppression groupe:', err);
    alert(err?.message || 'Impossible de supprimer ce groupe');
  }
}
isGroupCreator(channel: any): boolean {
  return channel.data?.createdByUserId === this.currentUserId;
}

async deleteOrLeaveGroup(channel: any, event?: Event): Promise<void> {
  event?.stopPropagation();

  try {

    if (this.isGroupCreator(channel)) {

      const ok = confirm(
        `Supprimer le groupe "${channel.data?.name || 'Sans nom'}" ?`
      );

      if (!ok) return;

      await channel.delete();

      console.log('Groupe supprimé');

    } else {

      const ok = confirm(
        `Quitter le groupe "${channel.data?.name || 'Sans nom'}" ?`
      );

      if (!ok) return;

      await channel.removeMembers([this.currentUserId]);

      console.log('Groupe quitté');
    }

    this.groupChannels =
      this.groupChannels.filter(g => g.id !== channel.id);

    if (this.activeChannel?.id === channel.id) {
      this.activeChannel = null;
    }

  } catch (err: any) {

    console.error('Erreur groupe:', err);

    alert(err?.message || 'Action impossible');
  }
}
listenForMessages(): void {

  this.chatService.chatClient.on('message.new', (event: any) => {

    const senderId = event.user?.id;
    const channelId = event.channel_id;

    // ignorer ses propres messages
    if (senderId === this.currentUserId) {
      return;
    }

    // conversation pas ouverte
    if (this.activeChannel?.id !== channelId) {

      this.unreadByChannel[channelId] =
        (this.unreadByChannel[channelId] || 0) + 1;

      if (senderId) {
        this.unreadByUser[senderId] =
          (this.unreadByUser[senderId] || 0) + 1;
      }
    }

    console.log('Nouveau message notification');
  });

}
onIncomingCall(channelId: string, userId?: string): void {

  this.incomingCallChannelId = channelId;

  if (userId) {
    this.incomingCallUserId = userId;
  }

}
}