import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivityService } from '../../services/activity.service';
import { Activity as ActivityModel } from '../../models/activity.model';
import { Router } from '@angular/router';
import { I18nService } from '../../services/i18n.service';

interface Slide {
  image: string;
  alt: string;
}

interface ActivityView {
  id: number;
  category: string;
  photo: string;
  meta: string;
  title: string;
  description: string;
  day: string;
  location: string;
  price: string;
  mediaClass?: string;
  pillClass?: string;
  prixHoraire?: number;
  days: string[];
  locations: string[];
}

@Component({
  selector: 'app-accueil',
  standalone: false,
  templateUrl: './accueil.html',
  styleUrls: ['./accueil.css']
})
export class AccueilComponent implements OnInit {

  role = '';
  isLoggedIn = false;
  loading = false;
  erreur = '';

  categories: string[] = [];
  jours: string[] = [];

  filters = {
    q: '',
    cat: '',
    jour: '',
    tri: 'debut',
    quick: ''
  };

  slides: Slide[] = [
    { image: 'https://picsum.photos/id/1011/1200/700', alt: 'Activité 1' },
    { image: 'https://picsum.photos/id/1015/1200/700', alt: 'Activité 2' },
    { image: 'https://picsum.photos/id/1016/1200/700', alt: 'Activité 3' },
    { image: 'https://picsum.photos/id/1025/1200/700', alt: 'Activité 4' }
  ];

  currentSlide = 0;

  activities: ActivityView[] = [];
  filteredActivities: ActivityView[] = [];
@ViewChild('carouselViewport')
carouselViewport!: ElementRef<HTMLDivElement>;

  constructor(
    private activityService: ActivityService,
    private router: Router,
    public i18n: I18nService
  ) {}


  ngOnInit(): void {
    this.loadActivities();
    this.refreshLoginState();
  }

  refreshLoginState(): void {
    this.role = localStorage.getItem('role') || '';
    this.isLoggedIn = !!localStorage.getItem('userId');
  }

  loadActivities(): void {
    this.loading = true;
    this.erreur = '';

    this.activityService.getActiveActivitiesWithSessions().subscribe({
      next: (data) => {
        this.activities = data
          .filter(activity => activity.sessionCount && activity.sessionCount > 0)
          .map((activity, index) => this.mapActivityToView(activity, index));

        this.filteredActivities = [...this.activities];

        this.categories = [
          ...new Set(this.activities.map(a => a.category).filter(Boolean))
        ];

        this.jours = [
          ...new Set(this.activities.flatMap(a => a.days).filter(Boolean))
        ];

        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement activités accueil', err);
        this.erreur = 'Erreur lors du chargement des activités.';
        this.loading = false;
      }
    });
  }

  mapActivityToView(activity: ActivityModel, index: number): ActivityView {
    const sessions = activity.sessions ?? [];

    const days = [
      ...new Set(
        sessions.map(s =>
          new Date(s.startDate.replace(' ', 'T'))
            .toLocaleDateString('fr-CA', { weekday: 'long' })
            .toLowerCase()
        )
      )
    ];

    const locations = [
      ...new Set(sessions.map(s => s.location).filter(Boolean))
    ];

    return {
      id: activity.id ?? 0,
      category: activity.categorie || 'Sans catégorie',
      photo: activity.photo || '',
      meta: `${sessions.length} session(s) disponible(s)`,
      title: activity.name,
      description: activity.description || 'Aucune description disponible.',
      day: days.length ? days.join(', ') : 'À déterminer',
      location: locations.length ? locations.join(', ') : 'À déterminer',
      price: activity.prixHoraire && activity.prixHoraire > 0
        ? `${activity.prixHoraire} $ / h`
        : '—',
      mediaClass: this.getMediaClass(index),
      pillClass: this.getPillClass(activity.categorie),
      prixHoraire: activity.prixHoraire ?? 0,
      days,
      locations
    };
  }

  applyFilters(): void {
    let result = [...this.activities];

    if (this.filters.q.trim()) {
      const q = this.filters.q.toLowerCase();

      result = result.filter(activity =>
        activity.title.toLowerCase().includes(q) ||
        activity.description.toLowerCase().includes(q) ||
        activity.category.toLowerCase().includes(q) ||
        activity.location.toLowerCase().includes(q)
      );
    }

    if (this.filters.cat) {
      result = result.filter(activity => activity.category === this.filters.cat);
    }

    if (this.filters.jour) {
      result = result.filter(activity =>
        activity.days.includes(this.filters.jour.toLowerCase())
      );
    }

    if (this.filters.quick === 'Gratuit') {
      result = result.filter(activity => activity.price === '—');
    }

    if (this.filters.quick === 'Enfants') {
      result = result.filter(activity =>
        activity.category.toLowerCase().includes('enfant') ||
        activity.title.toLowerCase().includes('enfant')
      );
    }

    if (this.filters.quick === 'Aînés') {
      result = result.filter(activity =>
        activity.category.toLowerCase().includes('aîné') ||
        activity.category.toLowerCase().includes('aine') ||
        activity.title.toLowerCase().includes('aîné') ||
        activity.title.toLowerCase().includes('aine')
      );
    }

    if (this.filters.tri === 'prixAsc') {
      result.sort((a, b) => (a.prixHoraire ?? 0) - (b.prixHoraire ?? 0));
    } else if (this.filters.tri === 'prixDesc') {
      result.sort((a, b) => (b.prixHoraire ?? 0) - (a.prixHoraire ?? 0));
    } else {
      result.sort((a, b) => a.title.localeCompare(b.title));
    }

    this.filteredActivities = result;
  }

  setQuickFilter(value: string): void {
    this.filters.quick = value;
    this.applyFilters();
  }

  getMediaClass(index: number): string {
    const classes = ['', 'card__media--alt', 'card__media--alt2'];
    return classes[index % classes.length];
  }

  getPillClass(category?: string): string {
    if (!category) return '';

    const cat = category.toLowerCase();

    if (cat.includes('bien')) return 'pill--green';
    if (cat.includes('sport')) return 'pill--amber';
    if (cat.includes('famille')) return 'pill--green';

    return '';
  }
goToSlide(index: number): void {
  this.currentSlide = index;

  const viewport = this.carouselViewport.nativeElement;

  viewport.scrollTo({
    left: viewport.clientWidth * index,
    behavior: 'smooth'
  });
}

prevSlide(): void {
  const index =
    this.currentSlide === 0
      ? this.slides.length - 1
      : this.currentSlide - 1;

  this.goToSlide(index);
}

nextSlide(): void {
  const index =
    this.currentSlide === this.slides.length - 1
      ? 0
      : this.currentSlide + 1;

  this.goToSlide(index);
}
}