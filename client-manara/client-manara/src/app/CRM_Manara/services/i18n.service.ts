import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type Lang = 'fr' | 'en';

const T: Record<Lang, Record<string, string>> = {
  fr: {
    // Navigation accueil
    'nav.home':          'Accueil',
    'nav.programming':   'Programmation',
    'nav.parents':       'Parents',
    'nav.trainer':       'Animateur',
    'nav.events':        'Événements',
    'nav.about':         'À propos',
    'nav.donate':        'Faites un don',
    'nav.policies':      'Politiques',

    // Hero accueil
    'hero.title':        'Programmation – Activités',
    'hero.subtitle':     'Des activités pour tous : culture, sports, bien-être et ateliers. Trouvez votre prochaine activité et inscrivez-vous facilement en quelques clics.',
    'hero.cta1':         'Voir les activités',
    'hero.cta2':         'Consulter les politiques',

    // Boutons globaux
    'btn.logout':        'Se déconnecter',
    'btn.login':         'Se connecter',
    'btn.create-account':'Créer un compte',
    'btn.save':          'Enregistrer',
    'btn.cancel':        'Annuler',
    'btn.confirm':       'Confirmer',
    'btn.register':      'Inscrire',
    'btn.details':       'Détails',
    'btn.add-child':     'Ajouter un enfant',

    // Rôles / sous-titres
    'greeting':          'Bonjour',
    'welcome':           'Bienvenue',
    'common.user':       'Utilisateur',
    'common.admin':      'Admin',
    'common.language':   'Langue',
    'common.french':     'Français',
    'common.english':    'English',
    'theme.light':       'Mode clair',
    'theme.dark':        'Mode sombre',
    'brand.subtitle':    'Centre d\'activités communautaires',
    'footer.contact':    'Nous joindre',
    'footer.hours':      'Heures d\'ouverture',
    'footer.weekdays':   'Lundi au vendredi :',
    'footer.weekend':    'Samedi et dimanche : Fermé',
    'footer.quick-links':'Liens rapides',
    'footer.rights':     '© 2026 - Tous droits réservés',
    'footer.privacy':    'Politique de confidentialité',
    'filter.label':      'Filtres',
    'filter.search':     'Rechercher',
    'filter.search-ph':  'Ex.: yoga, danse, tricot...',
    'filter.category':   'Catégorie',
    'filter.all':        'Toutes',
    'filter.day':        'Jour',
    'filter.every-day':  'Tous',
    'filter.sort':       'Trier',
    'filter.next-start': 'Prochain début',
    'filter.price-asc':  'Prix (croissant)',
    'filter.price-desc': 'Prix (décroissant)',
    'filter.apply':      'Appliquer',
    'filter.quick':      'Filtres rapides',
    'filter.children':   'Enfants',
    'filter.seniors':    'Aînés',
    'filter.free':       'Gratuit',
    'filter.new':        'Nouveauté',
    'carousel.gallery':  'Galerie',
    'carousel.prev':     'Précédent',
    'carousel.next':     'Suivant',
    'carousel.nav':      'Navigation du carrousel',
    'carousel.goto':     'Aller à la diapo',
    'activity.list':     'Liste des activités',
    'activity.photo':    'Photo de',
    'activity.cost':     'Coût',
    'activity.signup':   'Inscrivez-vous',
    'activity.help-title':'Besoin d\'aide pour choisir?',
    'activity.help-text': 'Contactez-nous pour être orienté vers l\'activité qui correspond à votre âge, votre horaire et vos objectifs.',
    'activity.contact':  'Nous joindre',
    'day.monday':        'Lundi',
    'day.tuesday':       'Mardi',
    'day.wednesday':     'Mercredi',
    'day.thursday':      'Jeudi',
    'day.friday':        'Vendredi',
    'day.saturday':      'Samedi',
    'day.sunday':        'Dimanche',
    'category.wellness': 'Bien-être',
    'category.sports':   'Sports',
    'category.culture':  'Culture',
    'category.family':   'Famille',
    'activity.1.meta':   'Débute le 15 sept.',
    'activity.1.title':  'Danse créative',
    'activity.1.desc':   'Activité ludique pour développer coordination, créativité et confiance à travers le mouvement.',
    'activity.1.day':    'Lundi · 18 h - 19 h',
    'activity.2.meta':   'Session hiver',
    'activity.2.title':  'Yoga express',
    'activity.2.desc':   'Une séance courte et efficace pour bouger, respirer et relâcher les tensions.',
    'activity.2.day':    'Mercredi · 12 h 10 - 12 h 40',
    'activity.3.meta':   'Places limitées',
    'activity.3.title':  'Qi Gong',
    'activity.3.desc':   'Entraînement doux pour l\'équilibre, la mobilité et l\'énergie, accessible à tous les niveaux.',
    'activity.3.day':    'Mardi · 9 h - 10 h',
    'role.parent':       'Parent',
    'role.parent-sub':   'Espace parent',
    'role.trainer':      'Animateur',
    'role.trainer-sub':  'Espace animateur',
    'role.admin':        'Administration',
    'role.admin-sub':    'Compte administrateur',

    // Navigation portails
    'nav.dashboard':     'Tableau de bord',
    'nav.children':      'Mes enfants',
    'nav.activities':    'Activités',
    'nav.registrations': 'Inscriptions',
    'nav.planning':      'Planning',
    'nav.notifications': 'Notifications',
    'nav.profile':       'Mon profil',
    'nav.cart':          'Mon panier',
    'nav.add-child-session': 'Ajouter un enfant à une session',
    'nav.sessions':      'Mes sessions',
    'nav.attendance':    'Présences',
    'nav.users':         'Utilisateurs',

    // Groupes sidebar
    'grp.main':          'Principal',
    'grp.account':       'Compte',
    'grp.management':    'Gestion',

    // Tableau
    'table.child':       'Enfant',
    'table.activity':    'Activité',
    'table.date':        'Date',
    'table.status':      'Statut',
    'table.location':    'Lieu',
    'table.attendance':  'Présence',
    'table.role':        'Rôle',
    'table.parent':      'Parent',

    // Statuts
    'status.confirmed':  'Confirmée',
    'status.pending':    'En attente',
    'status.cancelled':  'Annulée',
    'status.waitlist':   "Liste d'attente",
    'status.active':     'Actif',
    'status.inactive':   'Inactif',
    'status.present':    'Présent',
    'status.absent':     'Absent',

    // Stats
    'stats.children':    'Enfants',
    'stats.active':      'Inscriptions actives',
    'stats.pending':     'En attente',
    'stats.notifs':      'Notifications',
    'stats.sessions':    'Sessions cette semaine',
    'stats.enrolled':    'Enfants inscrits',
    'stats.assigned':    'Activités assignées',
    'stats.parents':     'Parents inscrits',
    'stats.allReg':      'Total inscriptions',
    'stats.thisMonth':   '+2 ce mois',
    'stats.thisWeek':    '+1 cette semaine',

    // Formulaires
    'form.firstname':    'Prénom',
    'form.lastname':     'Nom',
    'form.email':        'Adresse courriel',
    'form.phone':        'Téléphone',
    'form.birthdate':    'Date de naissance',
    'form.allergies':    'Allergies',
    'form.password':     'Mot de passe',
    'form.subject':      'Sujet du message',
    'form.message':      'Message...',

    // Notifications admin
    'notif.send':        'Envoyer un message',
    'notif.all-parents': 'Tous les parents',
    'notif.one-parent':  'Un parent en particulier',
    'notif.send-btn':    'Envoyer',
    'notif.history':     'Envois récents',
    'notif.recipient':   'Destinataire',

    // Toasts
    'toast.registered':  'Inscription confirmée !',
    'toast.cancelled':   'Inscription annulée.',
    'toast.attendance':  'Présences enregistrées !',
    'toast.notif-sent':  'Notification envoyée !',
    'toast.saved':       'Profil mis à jour !',
    'toast.created':     'Créé avec succès !',

    // Empty states
    'empty.children':    'Aucun enfant enregistré',
    'empty.children-sub':'Ajoutez un enfant pour vous inscrire aux activités.',
    'empty.sessions':    'Aucune session aujourd\'hui',
    'empty.sessions-sub':'Profitez-en pour préparer votre prochaine activité.',
  },

  en: {
    // Navigation accueil
    'nav.home':          'Home',
    'nav.programming':   'Programs',
    'nav.parents':       'Parents',
    'nav.trainer':       'Trainer',
    'nav.events':        'Events',
    'nav.about':         'About',
    'nav.donate':        'Donate',
    'nav.policies':      'Policies',

    // Hero accueil
    'hero.title':        'Programs – Activities',
    'hero.subtitle':     'Activities for everyone: culture, sports, wellness and workshops. Find your next activity and register in just a few clicks.',
    'hero.cta1':         'See activities',
    'hero.cta2':         'View policies',

    // Boutons globaux
    'btn.logout':        'Sign out',
    'btn.login':         'Sign in',
    'btn.create-account':'Create account',
    'btn.save':          'Save',
    'btn.cancel':        'Cancel',
    'btn.confirm':       'Confirm',
    'btn.register':      'Register',
    'btn.details':       'Details',
    'btn.add-child':     'Add a child',

    // Rôles / sous-titres
    'greeting':          'Hello',
    'welcome':           'Welcome',
    'common.user':       'User',
    'common.admin':      'Admin',
    'common.language':   'Language',
    'common.french':     'French',
    'common.english':    'English',
    'theme.light':       'Light mode',
    'theme.dark':        'Dark mode',
    'brand.subtitle':    'Community activity center',
    'footer.contact':    'Contact us',
    'footer.hours':      'Opening hours',
    'footer.weekdays':   'Monday to Friday:',
    'footer.weekend':    'Saturday and Sunday: Closed',
    'footer.quick-links':'Quick links',
    'footer.rights':     '© 2026 - All rights reserved',
    'footer.privacy':    'Privacy policy',
    'filter.label':      'Filters',
    'filter.search':     'Search',
    'filter.search-ph':  'Ex.: yoga, dance, knitting...',
    'filter.category':   'Category',
    'filter.all':        'All',
    'filter.day':        'Day',
    'filter.every-day':  'Any',
    'filter.sort':       'Sort',
    'filter.next-start': 'Next start',
    'filter.price-asc':  'Price (low to high)',
    'filter.price-desc': 'Price (high to low)',
    'filter.apply':      'Apply',
    'filter.quick':      'Quick filters',
    'filter.children':   'Children',
    'filter.seniors':    'Seniors',
    'filter.free':       'Free',
    'filter.new':        'New',
    'carousel.gallery':  'Gallery',
    'carousel.prev':     'Previous',
    'carousel.next':     'Next',
    'carousel.nav':      'Carousel navigation',
    'carousel.goto':     'Go to slide',
    'activity.list':     'Activity list',
    'activity.photo':    'Photo of',
    'activity.cost':     'Cost',
    'activity.signup':   'Sign up',
    'activity.help-title':'Need help choosing?',
    'activity.help-text': 'Contact us and we will guide you toward the activity that fits your age, schedule and goals.',
    'activity.contact':  'Contact us',
    'day.monday':        'Monday',
    'day.tuesday':       'Tuesday',
    'day.wednesday':     'Wednesday',
    'day.thursday':      'Thursday',
    'day.friday':        'Friday',
    'day.saturday':      'Saturday',
    'day.sunday':        'Sunday',
    'category.wellness': 'Wellness',
    'category.sports':   'Sports',
    'category.culture':  'Culture',
    'category.family':   'Family',
    'activity.1.meta':   'Starts Sept. 15',
    'activity.1.title':  'Creative dance',
    'activity.1.desc':   'A playful activity to build coordination, creativity and confidence through movement.',
    'activity.1.day':    'Monday · 6 p.m. - 7 p.m.',
    'activity.2.meta':   'Winter session',
    'activity.2.title':  'Express yoga',
    'activity.2.desc':   'A short, effective class to move, breathe and release tension.',
    'activity.2.day':    'Wednesday · 12:10 p.m. - 12:40 p.m.',
    'activity.3.meta':   'Limited spots',
    'activity.3.title':  'Qi Gong',
    'activity.3.desc':   'Gentle training for balance, mobility and energy, accessible to all levels.',
    'activity.3.day':    'Tuesday · 9 a.m. - 10 a.m.',
    'role.parent':       'Parent',
    'role.parent-sub':   'Parent portal',
    'role.trainer':      'Trainer',
    'role.trainer-sub':  'Trainer portal',
    'role.admin':        'Administration',
    'role.admin-sub':    'Administrator account',

    // Navigation portails
    'nav.dashboard':     'Dashboard',
    'nav.children':      'My children',
    'nav.activities':    'Activities',
    'nav.registrations': 'Registrations',
    'nav.planning':      'Schedule',
    'nav.notifications': 'Notifications',
    'nav.profile':       'My profile',
    'nav.cart':          'My cart',
    'nav.add-child-session': 'Add a child to a session',
    'nav.sessions':      'My sessions',
    'nav.attendance':    'Attendance',
    'nav.users':         'Users',

    // Groupes sidebar
    'grp.main':          'Main',
    'grp.account':       'Account',
    'grp.management':    'Management',

    // Tableau
    'table.child':       'Child',
    'table.activity':    'Activity',
    'table.date':        'Date',
    'table.status':      'Status',
    'table.location':    'Location',
    'table.attendance':  'Attendance',
    'table.role':        'Role',
    'table.parent':      'Parent',

    // Statuts
    'status.confirmed':  'Confirmed',
    'status.pending':    'Pending',
    'status.cancelled':  'Cancelled',
    'status.waitlist':   'Waitlist',
    'status.active':     'Active',
    'status.inactive':   'Inactive',
    'status.present':    'Present',
    'status.absent':     'Absent',

    // Stats
    'stats.children':    'Children',
    'stats.active':      'Active registrations',
    'stats.pending':     'Pending',
    'stats.notifs':      'Notifications',
    'stats.sessions':    'Sessions this week',
    'stats.enrolled':    'Enrolled children',
    'stats.assigned':    'Assigned activities',
    'stats.parents':     'Registered parents',
    'stats.allReg':      'Total registrations',
    'stats.thisMonth':   '+2 this month',
    'stats.thisWeek':    '+1 this week',

    // Formulaires
    'form.firstname':    'First name',
    'form.lastname':     'Last name',
    'form.email':        'Email address',
    'form.phone':        'Phone',
    'form.birthdate':    'Date of birth',
    'form.allergies':    'Allergies',
    'form.password':     'Password',
    'form.subject':      'Message subject',
    'form.message':      'Message...',

    // Notifications admin
    'notif.send':        'Send a message',
    'notif.all-parents': 'All parents',
    'notif.one-parent':  'One specific parent',
    'notif.send-btn':    'Send',
    'notif.history':     'Recent sends',
    'notif.recipient':   'Recipient',

    // Toasts
    'toast.registered':  'Registration confirmed!',
    'toast.cancelled':   'Registration cancelled.',
    'toast.attendance':  'Attendance saved!',
    'toast.notif-sent':  'Notification sent!',
    'toast.saved':       'Profile updated!',
    'toast.created':     'Created successfully!',

    // Empty states
    'empty.children':    'No children registered',
    'empty.children-sub':'Add a child to register for activities.',
    'empty.sessions':    'No sessions today',
    'empty.sessions-sub':'Take this time to prepare your next activity.',
  }
};

@Injectable({ providedIn: 'root' })
export class I18nService {

  lang$ = new BehaviorSubject<Lang>('fr');

  constructor() {
    const saved = localStorage.getItem('manara-lang') as Lang;
    if (saved === 'fr' || saved === 'en') this.setLang(saved);
  }

  setLang(lang: Lang): void {
    this.lang$.next(lang);
    localStorage.setItem('manara-lang', lang);
    document.documentElement.setAttribute('lang', lang);
    document.documentElement.setAttribute('dir', 'ltr');
  }

  t(key: string): string {
    return T[this.lang$.value]?.[key] ?? T['fr']?.[key] ?? key;
  }

  get current(): Lang {
    return this.lang$.value;
  }
}
