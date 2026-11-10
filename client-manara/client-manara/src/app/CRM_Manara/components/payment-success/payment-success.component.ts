import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CartService } from '../../services/cart.service';

interface ReceiptItem {
  activityName: string;
  childName: string;
  startDate: string;
  endDate: string;
  location: string;
  prix: number;
}

interface ReceiptSnapshot {
  items: ReceiptItem[];
  total: number;
  date: string;
}

@Component({
  selector: 'app-payment-success',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-slate-50 flex items-start justify-center p-4 pt-12">
      <div class="w-full max-w-xl">

        <!-- En-tête succès -->
        <div class="mb-6 flex flex-col items-center text-center">
          <div class="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-emerald-100">
            <svg class="h-8 w-8 text-emerald-600" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
              <polyline points="20 6 9 17 4 12"/>
            </svg>
          </div>
          <h1 class="text-2xl font-extrabold text-slate-900">Paiement confirmé !</h1>
          <p class="mt-1 text-sm text-slate-500">
            Merci. Vos inscriptions sont confirmées et un reçu a été envoyé par email.
          </p>
          <p *ngIf="sessionId" class="mt-2 rounded-full bg-slate-100 px-3 py-1 text-xs font-mono text-slate-500">
            Réf. Stripe : {{ sessionId }}
          </p>
        </div>

        <!-- Reçu -->
        <div class="rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden" id="receipt-print">

          <!-- Bandeau reçu -->
          <div class="bg-emerald-600 px-6 py-4 flex items-center justify-between">
            <span class="text-sm font-bold text-white">Reçu de paiement</span>
            <span class="text-xs text-emerald-100">{{ receiptDate }}</span>
          </div>

          <!-- Items -->
          <div class="divide-y divide-slate-100">
            <div *ngFor="let item of items" class="px-6 py-4 flex flex-col sm:flex-row sm:justify-between gap-1">
              <div>
                <p class="font-bold text-slate-900 text-sm">{{ item.activityName }}</p>
                <p class="text-xs text-slate-500">Enfant : {{ item.childName }}</p>
                <p class="text-xs text-slate-400">
                  {{ item.startDate | date:'d MMM yyyy' }} →
                  {{ item.endDate   | date:'d MMM yyyy' }}
                </p>
                <p *ngIf="item.location" class="text-xs text-slate-400">{{ item.location }}</p>
              </div>
              <p class="text-sm font-extrabold text-slate-900 self-center">
                {{ item.prix | currency:'CAD' }}
              </p>
            </div>
          </div>

          <!-- Total -->
          <div class="flex justify-between items-center border-t border-slate-200 px-6 py-4 bg-slate-50">
            <span class="text-sm font-bold text-slate-700">Total payé</span>
            <span class="text-xl font-extrabold text-emerald-700">{{ total | currency:'CAD' }}</span>
          </div>
        </div>

        <!-- Actions -->
        <div class="mt-6 flex flex-col sm:flex-row gap-3">
          <button
            type="button"
            (click)="imprimer()"
            class="flex-1 inline-flex items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-5 py-3 text-sm font-bold text-slate-700 hover:bg-slate-50 transition shadow-sm">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"/>
            </svg>
            Imprimer le reçu
          </button>

          <button
            type="button"
            (click)="allerNotifications()"
            class="flex-1 inline-flex items-center justify-center gap-2 rounded-xl bg-emerald-600 px-5 py-3 text-sm font-bold text-white hover:bg-emerald-700 transition shadow-sm">
            Voir mes notifications
          </button>

          <button
            type="button"
            (click)="retourAccueil()"
            class="flex-1 inline-flex items-center justify-center gap-2 rounded-xl bg-slate-900 px-5 py-3 text-sm font-bold text-white hover:bg-slate-800 transition shadow-sm">
            Retour à l'accueil
          </button>
        </div>

      </div>
    </div>
  `
})
export class PaymentSuccessComponent implements OnInit {

  sessionId = '';
  items: ReceiptItem[] = [];
  total = 0;
  receiptDate = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.sessionId = this.route.snapshot.queryParamMap.get('session_id') ?? '';

    const raw = localStorage.getItem('receipt_snapshot');
    if (raw) {
      try {
        const snapshot: ReceiptSnapshot = JSON.parse(raw);
        this.items = snapshot.items;
        this.total = snapshot.total;
        this.receiptDate = new Date(snapshot.date).toLocaleDateString('fr-CA', {
          year: 'numeric', month: 'long', day: 'numeric'
        });
      } catch {
        this.items = [];
      }
    }

    this.cartService.clearCart();
    localStorage.removeItem('receipt_snapshot');
  }

  imprimer(): void {
    window.print();
  }

  allerNotifications(): void {
    this.router.navigate(['/parent/notifications']);
  }

  retourAccueil(): void {
    this.router.navigate(['/']);
  }
}
