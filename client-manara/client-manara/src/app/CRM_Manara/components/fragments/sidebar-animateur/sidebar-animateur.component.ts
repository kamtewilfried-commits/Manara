import { Component, Input } from '@angular/core';
import { I18nService } from '../../../services/i18n.service';

@Component({
  selector: 'app-sidebar-animateur',
  standalone: false,
  templateUrl: './sidebar-animateur.component.html'
})
export class SidebarAnimateurComponent {
  @Input() activePage: string = '';
  @Input() connectedUser: any;

  constructor(public i18n: I18nService) {}
}
