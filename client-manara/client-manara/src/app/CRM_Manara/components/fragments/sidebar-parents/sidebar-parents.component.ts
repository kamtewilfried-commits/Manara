import { Component, Input, OnInit } from '@angular/core';
import { I18nService } from '../../../services/i18n.service';

@Component({
  selector: 'app-sidebar-parent',
  standalone: false,
  templateUrl: './sidebar-parents.component.html'
})
export class SidebarParentsComponent implements OnInit {
  @Input() activePage: string = '';
  @Input() connectedUser: any;
  @Input() unreadNotifications: number = 0;

  email = '';

  constructor(public i18n: I18nService) {}

  ngOnInit(): void {
    this.email = localStorage.getItem('email') || '';
  }

  getDisplayName(): string {
    if (this.connectedUser?.firstName || this.connectedUser?.lastName) {
      return `${this.connectedUser?.firstName || ''} ${this.connectedUser?.lastName || ''}`.trim();
    }

    return this.email ? this.email.split('@')[0] : 'Parent';
  }
}
