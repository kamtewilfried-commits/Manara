import { NgModule, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { TranslateModule } from '@ngx-translate/core';

import { AppRoutingModule } from './app-routing-module';
import { BaseChartDirective } from 'ng2-charts';

import {
  StreamChatModule,
  VoiceRecorderModule,
  StreamAutocompleteTextareaModule
} from 'stream-chat-angular';
import { MessageModule } from './message.module';

import { App } from './app';

// Admin
import { DashboardAdmin }     from './CRM_Manara/components/Admin/dashboard/dashboard';
import { ActivitiesAdmin }    from './CRM_Manara/components/Admin/activities/activities';
import { NotificationsAdmin } from './CRM_Manara/components/Admin/notifications/notifications';
import { RegistrationsAdmin } from './CRM_Manara/components/Admin/registrations/registrations';
import { SessionsAdmin }      from './CRM_Manara/components/Admin/sessions/sessions';
import { Users }              from './CRM_Manara/components/Admin/users/users';

// Animateur
import { Dashboard }             from './CRM_Manara/components/Animateur/dashboard/dashboard';
import { ProfileAnimateur }      from './CRM_Manara/components/Animateur/profil/profil';
import { Presences }             from './CRM_Manara/components/Animateur/presences/presences';
import { SessionsAnimateur }     from './CRM_Manara/components/Animateur/sessions/sessions';
import { RegistrationAnimateur } from './CRM_Manara/components/Animateur/registrations/registration';
import { PlanningAnimateur } from './CRM_Manara/components/Animateur/planning/planning';

// Parent
import { Activities }    from './CRM_Manara/components/Parent/activities/activities';
import { Children }      from './CRM_Manara/components/Parent/children/children';
import { Notifications } from './CRM_Manara/components/Parent/notifications/notifications';
import { PlanningParent }from './CRM_Manara/components/Parent/planning/planning';
import { Profile }       from './CRM_Manara/components/Parent/profile/profile';
import { Registrations } from './CRM_Manara/components/Parent/registrations/registrations';

// Pages publiques
import { AccueilComponent }      from './CRM_Manara/components/accueil/accueil';
import { LoginComponent }        from './CRM_Manara/components/login/login';
import { RegisterComponent }     from './CRM_Manara/components/register/register';
import { VerificationComponent } from './CRM_Manara/components/verification/verification.component';
import { VerifyEmailComponent } from './CRM_Manara/components/verification/verify-email-component';
import { SessionComponent }      from './CRM_Manara/components/session.component';
import { CartComponent }         from './CRM_Manara/components/cart/cart.component';

// Fragments
import { HeaderAcceuilComponent }   from './CRM_Manara/components/fragments/header-acceuil/header-acceuil.component';
import { FooterAcceuilComponent }   from './CRM_Manara/components/fragments/footer-acceuil/footer-acceuil.component';
import { HeaderAdminComponent }     from './CRM_Manara/components/fragments/header-admin/header-admin.component';
import { HeaderAnimateurComponent } from './CRM_Manara/components/fragments/header-animateur/header-animateur.component';
import { HeaderParentsComponent }   from './CRM_Manara/components/fragments/header-parents/header-parents.component';
import { SidebarParentsComponent }  from './CRM_Manara/components/fragments/sidebar-parents/sidebar-parents.component';
import { SidebarAnimateurComponent }from './CRM_Manara/components/fragments/sidebar-animateur/sidebar-animateur.component';
import { SidebarAdminComponent }    from './CRM_Manara/components/fragments/sidebar-admin/sidebar-admin.component';
import { ChatWidgetComponent }      from './CRM_Manara/components/fragments/chat-widget/chat-widget.component';

// Chat / Video
import { ChatComponent }      from './CRM_Manara/ChatComponents/chat.component';
import { VideoCallComponent } from './CRM_Manara/ChatComponents/video-call.component';
// ← MessageComponent retiré : déjà déclaré dans MessageModule

// Pipe custom
import { TranslatePipe } from './CRM_Manara/pipes/translate.pipe';
import { SharedModule } from './shared.module';
import { OAuthSuccessComponent } from './CRM_Manara/components/login/oat-success.component';

@NgModule({
  declarations: [
    App,
    CartComponent,

    // Admin
    DashboardAdmin, ActivitiesAdmin, NotificationsAdmin,
    RegistrationsAdmin, SessionsAdmin, Users,

    // Animateur
    Dashboard, ProfileAnimateur, Presences,
    SessionsAnimateur, RegistrationAnimateur, PlanningAnimateur,

    // Parent
    Activities, Children, Notifications,
    PlanningParent, Profile, Registrations,

    // Pages publiques
    AccueilComponent, LoginComponent, RegisterComponent,
    VerificationComponent, VerifyEmailComponent, SessionComponent, OAuthSuccessComponent,

    // Fragments
    HeaderAcceuilComponent, FooterAcceuilComponent,
    HeaderAdminComponent, HeaderAnimateurComponent,
    HeaderParentsComponent, SidebarParentsComponent,
    SidebarAnimateurComponent, SidebarAdminComponent,
    ChatWidgetComponent,

    // Chat / Video
    // ← MessageComponent absent ici car il est dans MessageModule
    ChatComponent, VideoCallComponent,

  ],

  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CommonModule,
    HttpClientModule,
    RouterModule,
    BaseChartDirective,
    StreamChatModule,
    StreamAutocompleteTextareaModule,
    VoiceRecorderModule,
    TranslateModule.forRoot(),
    MessageModule,
    SharedModule,  // ← MessageComponent vient de là
  ],

  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
  ],

  bootstrap: [App]
})
export class AppModule {}