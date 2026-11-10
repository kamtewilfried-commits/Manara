import { Component, Input } from '@angular/core';
import { I18nService } from '../../../services/i18n.service';

@Component({
  selector: 'app-sidebar-admin',
  standalone: false,
  templateUrl: './sidebar-admin.component.html'
})
export class SidebarAdminComponent {
  @Input() activePage: string = '';
  isOpen = false;

  constructor(public i18n: I18nService) {}

  toggleSidebar() {
    this.isOpen = !this.isOpen;
  }
}
