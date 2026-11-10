import {
  AfterViewChecked,
  Component,
  ElementRef,
  ViewChild
} from '@angular/core';
import { ChatbotService, ChatResponse } from '../../../services/chatbot.service';

interface ChatMessage {
  text: string;
  sender: 'user' | 'bot';
}

@Component({
  selector: 'app-chat-widget',
  standalone: false,
  templateUrl: './chat-widget.component.html',
  styleUrls: ['./chat-widget.component.css']
})
export class ChatWidgetComponent implements AfterViewChecked {
  @ViewChild('messagesContainer') messagesContainer?: ElementRef;

  isOpen = false;
  isTyping = false;
  isListening = false;
  currentMessage = '';

  messages: ChatMessage[] = [
    {
      text: 'Bonjour 👋\nJe peux répondre sur les activités, sessions et horaires.',
      sender: 'bot'
    }
  ];

  constructor(private chatService: ChatbotService) {}

  toggleChat(): void {
    this.isOpen = !this.isOpen;
  }

  closeChat(): void {
    this.isOpen = false;
  }

  sendMessage(): void {
    const message = this.currentMessage.trim();
    if (!message) return;

    this.sendToChatbot(message);
    this.currentMessage = '';
  }

 startVoice(): void {
  console.log("Micro lancé");

  const SpeechRecognition =
    (window as any).SpeechRecognition ||
    (window as any).webkitSpeechRecognition;

  if (!SpeechRecognition) {
    alert('Reconnaissance vocale non supportée sur ce navigateur.');
    return;
  }

  const recognition = new SpeechRecognition();
  recognition.lang = 'fr-CA';
  recognition.interimResults = false;

  recognition.onstart = () => {
    console.log("🎙️ écoute démarrée");
    this.isListening = true;
  };

  recognition.onresult = (event: any) => {
    const voiceText = event.results[0][0].transcript;
    console.log('VOIX RECONNUE:', voiceText);

    this.sendToChatbot(voiceText, true);
  };

recognition.onerror = (event: any) => {
  console.error("Erreur micro:", event.error);
  this.isListening = false;

  if (event.error === 'network') {
    this.messages.push({
      text: "La reconnaissance vocale est indisponible pour l’instant. Vérifie ta connexion, ton navigateur ou ton VPN.",
      sender: 'bot'
    });
  }
};
  recognition.onend = () => {
    console.log("🛑 écoute arrêtée");
    this.isListening = false;
  };

  recognition.start();
}

  sendToChatbot(message: string, speakResponse = false): void {
  this.messages.push({
    text: message,
    sender: 'user'
  });

  this.isTyping = true;

  this.chatService.sendMessage(message).subscribe({
    next: (response: ChatResponse) => {
      this.isTyping = false;

      const botText = response.reply || 'Je n’ai pas pu répondre.';

      this.messages.push({
        text: botText,
        sender: 'bot'
      });

      if (speakResponse) {
        this.speak(botText);
      }
    },
    error: () => {
      this.isTyping = false;

      const errorText = 'Erreur de communication avec le serveur.';

      this.messages.push({
        text: errorText,
        sender: 'bot'
      });

      if (speakResponse) {
        this.speak(errorText);
      }
    }
  });
}

  speak(text: string): void {
    speechSynthesis.cancel();

    const voice = new SpeechSynthesisUtterance(text);
    voice.lang = 'fr-CA';

    speechSynthesis.speak(voice);
  }
testVoice(): void {
  speechSynthesis.speak(new SpeechSynthesisUtterance("Test audio"));
}
  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    if (this.messagesContainer) {
      const el = this.messagesContainer.nativeElement;
      el.scrollTop = el.scrollHeight;
    }
  }
}