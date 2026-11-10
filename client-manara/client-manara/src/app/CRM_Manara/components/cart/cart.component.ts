import { Component, OnInit } from '@angular/core';
import { CartService } from '../../services/cart.service';
import { CheckoutService } from '../../services/checkout.service';
import { CartItem } from '../../models/cart-item.model';
import { UserService } from '../../services/user.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-cart',
  standalone: false,
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {

  connectedUser: any = null;
  unreadNotifications = 0;

  cart: CartItem[] = [];
  total = 0;
  errorMessage = '';

  constructor(
    private cartService: CartService,
    private checkoutService: CheckoutService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadConnectedParent();
    this.loadCart();
    this.loadUnreadNotifications();
  }

  loadUnreadNotifications(): void {
    const userId = Number(localStorage.getItem('userId'));
    if (!userId) return;

    this.notificationService.countUnread(userId).subscribe({
      next: (count) => this.unreadNotifications = count,
      error: () => this.unreadNotifications = 0
    });
  }

  loadConnectedParent(): void {
    const email = localStorage.getItem('email');

    if (!email) {
      this.errorMessage = 'Parent non connecté.';
      return;
    }

    this.userService.getUserByEmail(email).subscribe({
      next: (user) => {
        this.connectedUser = user;
      },
      error: (err) => {
        console.log('ERREUR USER EMAIL =', err);
        this.errorMessage = 'Impossible de récupérer le parent connecté.';
      }
    });
  }

  loadCart(): void {
    this.cart = this.cartService.getCart();
    this.total = this.cartService.getTotal();
  }

  remove(item: CartItem): void {
    this.cartService.removeFromCart(item.sessionId, item.childId);
    this.loadCart();
  }

  payer(): void {
    this.errorMessage = '';

    if (this.cart.length === 0) {
      this.errorMessage = 'Votre panier est vide.';
      return;
    }

    const snapshot = {
      items: this.cart,
      total: this.total,
      date: new Date().toISOString()
    };
    localStorage.setItem('receipt_snapshot', JSON.stringify(snapshot));

    const request = {
      items: this.cart.map(item => ({
        sessionId: item.sessionId,
        childId: item.childId
      }))
    };

    this.checkoutService.checkout(request).subscribe({
      next: (res) => {
        window.location.href = res.url;
      },
      error: (err) => {
        localStorage.removeItem('receipt_snapshot');
        this.errorMessage = err.error?.message || 'Erreur lors du paiement.';
      }
    });
  }
}