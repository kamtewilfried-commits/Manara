import { Injectable } from '@angular/core';
import { CartItem } from '../models/cart-item.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private key = 'registration_cart';

  getCart(): CartItem[] {
    try {
      return JSON.parse(localStorage.getItem(this.key) || '[]');
    } catch {
      return [];
    }
  }

  addToCart(item: CartItem): boolean {
    const cart = this.getCart();

    const exists = cart.some(
      c => c.sessionId === item.sessionId && c.childId === item.childId
    );

    if (exists) {
      return false;
    }

    
    item.prix = Number(item.prix || 0);

    cart.push(item);
    localStorage.setItem(this.key, JSON.stringify(cart));
    return true;
  }

  removeFromCart(sessionId: number, childId: number): void {
    const cart = this.getCart().filter(
      c => !(c.sessionId === sessionId && c.childId === childId)
    );

    localStorage.setItem(this.key, JSON.stringify(cart));
  }

  clearCart(): void {
    localStorage.removeItem(this.key);
  }

  getTotal(): number {
    return this.getCart().reduce(
      (sum, item) => sum + Number(item.prix || 0),
      0
    );
  }
}