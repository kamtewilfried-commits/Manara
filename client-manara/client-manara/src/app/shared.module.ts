import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from './CRM_Manara/pipes/translate.pipe';

/**
 * SharedModule — importer dans AppModule UNE SEULE FOIS
 * Il exporte TranslatePipe pour que TOUS les composants puissent l'utiliser
 */
@NgModule({
  declarations: [
    TranslatePipe       // déclaré ici une seule fois
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule
  ],
  exports: [
    TranslatePipe,      // ← exporté pour que AppModule puisse l'utiliser
    CommonModule,
    RouterModule,
    FormsModule
  ]
})
export class SharedModule {}