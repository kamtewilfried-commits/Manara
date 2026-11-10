import { Component } from '@angular/core';
import { I18nService } from '../../../services/i18n.service';

@Component({
  selector: 'app-footer-acceuil',
   standalone: false,
  templateUrl: './footer-acceuil.component.html',
  styleUrls: ['./footer-acceuil.component.css']
})
export class FooterAcceuilComponent {
  constructor(public i18n: I18nService) {}
}
