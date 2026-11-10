import { Pipe, PipeTransform, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { I18nService } from '../services/i18n.service';
import { Subscription } from 'rxjs';

@Pipe({
  name: 'translate',
  pure: false,
  standalone: false   // ← obligatoire pour déclarer dans NgModule
})
export class TranslatePipe implements PipeTransform, OnDestroy {

  private sub: Subscription;

  constructor(
    private i18n: I18nService,
    private cd: ChangeDetectorRef
  ) {
    this.sub = this.i18n.lang$.subscribe(() => {
      this.cd.markForCheck();
    });
  }

  transform(key: string): string {
    return this.i18n.t(key);
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}