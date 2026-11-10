import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './CRM_Manara/components/login/login';
import { RegisterComponent } from './CRM_Manara/components/register/register';

import { AccueilComponent } from './CRM_Manara/components/accueil/accueil';
import { Activities } from './CRM_Manara/components/Parent/activities/activities';
import { Dashboard } from './CRM_Manara/components/Animateur/dashboard/dashboard';
import { DashboardAdmin } from './CRM_Manara/components/Admin/dashboard/dashboard';
import { Users } from './CRM_Manara/components/Admin/users/users';
import { ActivitiesAdmin } from './CRM_Manara/components/Admin/activities/activities';
import { SessionsAdmin } from './CRM_Manara/components/Admin/sessions/sessions';
import { Notifications } from './CRM_Manara/components/Parent/notifications/notifications';
import { VerificationComponent } from './CRM_Manara/components/verification/verification.component';
import { ChatComponent } from './CRM_Manara/ChatComponents/chat.component';
import { CartComponent } from './CRM_Manara/components/cart/cart.component';
import { Children } from './CRM_Manara/components/Parent/children/children';
import { Registrations } from './CRM_Manara/components/Parent/registrations/registrations';
import {  PlanningParent } from './CRM_Manara/components/Parent/planning/planning';
import { Profile } from './CRM_Manara/components/Parent/profile/profile';
import { SessionComponent } from './CRM_Manara/components/session.component';
import { SessionsAnimateur } from './CRM_Manara/components/Animateur/sessions/sessions';
import { RegistrationAnimateur } from './CRM_Manara/components/Animateur/registrations/registration';
import { VerifyEmailComponent } from './CRM_Manara/components/verification/verify-email-component';
import { Presences } from './CRM_Manara/components/Animateur/presences/presences';
import { ProfileAnimateur } from './CRM_Manara/components/Animateur/profil/profil';
import { PlanningAnimateur } from './CRM_Manara/components/Animateur/planning/planning';
import { OAuthSuccessComponent } from './CRM_Manara/components/login/oat-success.component';
import { NotificationsAdmin } from './CRM_Manara/components/Admin/notifications/notifications';
import { RegistrationsAdmin } from './CRM_Manara/components/Admin/registrations/registrations';
import { PaymentSuccessComponent } from './CRM_Manara/components/payment-success/payment-success.component';


const routes: Routes = [
  { path: '', component: AccueilComponent},{
  path: 'oauth-success',
  component: OAuthSuccessComponent
},
   { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {path:'animateur/dashboard',component: Dashboard},
  {path:'dashboard',component: DashboardAdmin},
  {path:'parent/activites',component: Activities},
   {path:'utilisateurs',component:Users},
   {path:'activites',component:ActivitiesAdmin},
   {path:'sessions',component:SessionsAdmin},
   {path:'parent/notifications',component:Notifications},
   {path:'verification',component:VerificationComponent},
   {path:'chat',component:ChatComponent},
   {path:'cart',component:CartComponent},
   {path:'parent/children',component:Children},
   {path:'parent/registration',component:Registrations},
   //{path:'parent/dashboard',component:Pa}
   {path:'parent/planning',component:PlanningParent},
   {path:'parent/profile',component:Profile},
     {
  path: 'parent/activites/:activityId',
  component: SessionComponent
},
      {path:'animateur/sessions',component:SessionsAnimateur}
      ,{path:'animateur/registration',component:RegistrationAnimateur},
      {path:'animateur/presence',component:Presences},
      {path:'animateur/planning',component:PlanningAnimateur},
      {path:'animateur/profil',component:ProfileAnimateur},
      {path:'inscriptions',component:RegistrationsAdmin},
      {path:'admin/notifications',component:NotificationsAdmin},
      {
  path: 'verify-email',
  component: VerifyEmailComponent
},
{
  path: 'payment-success',
  component: PaymentSuccessComponent
}

  
];

@NgModule({
  imports: [ RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
