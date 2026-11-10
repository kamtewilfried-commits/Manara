import { Component, Input } from '@angular/core';
import { StreamMessage } from 'stream-chat-angular';

@Component({
  selector: 'app-message',
  standalone: false,
  template: `
    <div>
      <b>{{ message?.user?.name }}</b> {{ message?.text }}
    </div>
  `,
  styles: ['div { height: 50px } b { margin-right: 4px }'],
})
export class MessageComponent {
  @Input() message?: StreamMessage;
}