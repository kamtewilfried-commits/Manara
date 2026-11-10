import { Component, signal } from '@angular/core';
import { CartService } from './CRM_Manara/services/cart.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('client-manara');

   constructor(private cartService: CartService) {}

  ngOnInit(): void {
    const params = new URLSearchParams(window.location.search);

    if (params.get('success') === 'true') {
      this.cartService.clearCart();

    
      window.history.replaceState({}, document.title, '/');
    }
  }

}
