import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { StreamVideoClient, Call } from '@stream-io/video-client';

interface StreamTokenResponse {
  token: string;
  userId: string;
  name: string;
  email?: string;
}

@Component({
  selector: 'app-video-call',
  standalone: false,
  templateUrl: './video-call.component.html',
  styleUrls: ['./video-call.component.css']
})
export class VideoCallComponent implements OnDestroy {

  @Input() activeChannel!: any;
  @Input() currentUserId = '';
  @Input() currentUserName = '';
  @Input() apiKey = '';

  videoClient?: StreamVideoClient;
  call?: Call;
isMicEnabled = true;
isCameraEnabled = true;
  isCalling = false;
  remoteParticipants: any[] = [];

  isScreenSharing = false;

  @ViewChildren('remoteAudios')
remoteAudios!: QueryList<ElementRef<HTMLAudioElement>>;
@ViewChild('localScreenShare')
localScreenShare?: ElementRef<HTMLVideoElement>;

@ViewChildren('remoteScreenShares')
remoteScreenShares!: QueryList<ElementRef<HTMLVideoElement>>;

  @ViewChild('localVideo')
  localVideo?: ElementRef<HTMLVideoElement>;

  @ViewChildren('remoteVideos')
  remoteVideos!: QueryList<ElementRef<HTMLVideoElement>>;
  @Output() incomingCall = new EventEmitter<any>();

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  startCall(): void {
    this.incomingCall.emit({
  channelId: this.activeChannel.id,
  userId: this.currentUserId
});
    if (this.isCalling) {
      console.warn('Appel déjà en cours');
      return;
    }

    if (!this.currentUserId || !this.activeChannel) {
      console.error('Aucune conversation active');
      return;
    }

    this.http.get<StreamTokenResponse>(
      'http://localhost:8080/api/chatting/token',
      { withCredentials: true }
    ).subscribe({
      next: async (res) => {
        this.videoClient = StreamVideoClient.getOrCreateInstance({
          apiKey: this.apiKey,
          user: {
            id: res.userId,
            name: res.name || res.userId
          },
          token: res.token
        });

        const callId = `${this.activeChannel.id}-video`;
        this.call = this.videoClient.call('default', callId);

        await this.call.join({ create: true });
        await this.call.camera.enable();
        await this.call.microphone.enable();

        this.isCalling = true;

        this.call.on('participantJoined', () => this.scheduleBind());
        this.call.on('participantLeft', () => this.scheduleBind());
        this.call.on('trackPublished', () => this.scheduleBind());
        this.call.on('trackUnpublished', () => this.scheduleBind());

        this.scheduleBind();
      },
      error: (err) => {
        console.error('Erreur appel vidéo:', err);
      }
    });
  }
async toggleScreenShare(): Promise<void> {
  if (!this.call) return;

  try {
    if (this.isScreenSharing) {
      await this.call.screenShare.disable();
      this.isScreenSharing = false;
    } else {
      await this.call.screenShare.enable();
      this.isScreenSharing = true;
    }

    this.scheduleBind();
  } catch (e) {
    console.error('Erreur partage écran:', e);
  }
}

  private scheduleBind(): void {
    setTimeout(() => this.bindAllVideos(), 300);
  }

  private bindAllVideos(): void {
    const participants = this.call?.state.participants || [];

    console.log('CALL PARTICIPANTS:', participants.map(p => ({
      userId: p.userId,
      local: p.isLocalParticipant,
      sessionId: p.sessionId,
      tracks: p.publishedTracks
    })));

    const localParticipant = participants.find(p => p.isLocalParticipant);

    if (localParticipant && this.localVideo?.nativeElement) {
      this.call?.bindVideoElement(
        this.localVideo.nativeElement,
        localParticipant.sessionId,
        'videoTrack'
      );
    }
    if (
  localParticipant &&
  this.localScreenShare?.nativeElement &&
  (localParticipant.publishedTracks as any)?.includes(3)
) {
  this.call?.bindVideoElement(
    this.localScreenShare.nativeElement,
    localParticipant.sessionId,
    'screenShareTrack'
  );
}

  const uniqueRemoteUsers = new Map<string, any>();

participants
  .filter(p =>
    !p.isLocalParticipant &&
    p.userId !== this.currentUserId
  )
  .forEach(p => {
    if (!uniqueRemoteUsers.has(p.userId)) {
      uniqueRemoteUsers.set(p.userId, p);
    }
  });

this.remoteParticipants = Array.from(uniqueRemoteUsers.values());

    console.log('REMOTE DETAIL:', this.remoteParticipants.map(p => ({
      userId: p.userId,
      sessionId: p.sessionId,
      tracks: p.publishedTracks,
      hasVideoTrack: (p.publishedTracks as any)?.includes(2)
    })));

    this.cdr.detectChanges();

    setTimeout(() => this.bindRemoteVideos(), 0);
  }

  private bindRemoteVideos(): void {
    const videos = this.remoteVideos.toArray();
    const screenShares = this.remoteScreenShares.toArray();
    const audios = this.remoteAudios.toArray();

    console.log('REMOTE VIDEO ELEMENTS:', videos.length);

    this.remoteParticipants.forEach((participant, index) => {
const audio = audios[index];

if (audio && (participant.publishedTracks as any)?.includes(1)) {
  this.call?.bindAudioElement(
    audio.nativeElement,
    participant.sessionId,
    'audioTrack'
  );

  console.log('REMOTE AUDIO BINDED:', participant.userId);
}

      const video = videos[index];

      console.log('TRY BIND REMOTE VIDEO:', {
        userId: participant.userId,
        sessionId: participant.sessionId,
        tracks: participant.publishedTracks,
        hasElement: !!video
      });
// VIDEO
if (video && (participant.publishedTracks as any)?.includes(2)) {
  this.call?.bindVideoElement(
    video.nativeElement,
    participant.sessionId,
    'videoTrack'
  );
}

        console.log('REMOTE VIDEO BINDED:', participant.userId);

// SCREEN SHARE (séparé)
const screenShare = screenShares[index];

if (screenShare && (participant.publishedTracks as any)?.includes(3)) {
  this.call?.bindVideoElement(
    screenShare.nativeElement,
    participant.sessionId,
    'screenShareTrack'
  );
}

console.log('SCREEN SHARE CHECK:', this.remoteParticipants.map(p => ({
  userId: p.userId,
  hasScreen: (p.publishedTracks as any)?.includes(3)
})));
    });
  }

  async leaveCall(): Promise<void> {
    this.remoteParticipants = [];

    try {
      await this.call?.camera.disable();
      await this.call?.microphone.disable();
      await this.call?.leave();
    } catch (e) {
      console.warn('Erreur fermeture appel:', e);
    }

    try {
      await this.videoClient?.disconnectUser();
    } catch (e) {
      console.warn('Erreur déconnexion vidéo:', e);
    }

    this.call = undefined;
    this.videoClient = undefined;
    this.isCalling = false;
  }

  trackByParticipant(index: number, participant: any): string {
    return participant.sessionId;
  }

  ngOnDestroy(): void {
    this.leaveCall();
  }
  async toggleMicrophone(): Promise<void> {
  if (!this.call) return;

  if (this.isMicEnabled) {
    await this.call.microphone.disable();
  } else {
    await this.call.microphone.enable();
  }

  this.isMicEnabled = !this.isMicEnabled;
}

async toggleCamera(): Promise<void> {
  if (!this.call) return;

  if (this.isCameraEnabled) {
    await this.call.camera.disable();
  } else {
    await this.call.camera.enable();
  }

  this.isCameraEnabled = !this.isCameraEnabled;
}
async toggleFullscreen(): Promise<void> {

  const element = document.getElementById('video-area');

  if (!element) return;

  if (!document.fullscreenElement) {

    await element.requestFullscreen();

  } else {

    await document.exitFullscreen();

  }

}
}