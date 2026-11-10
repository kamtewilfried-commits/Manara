import { Component, OnInit } from '@angular/core';
import { AdminStatsService } from '../../../services/activit-registration-stats.service';

@Component({
  selector: 'app-dashboard-admin',
  standalone: false,
  templateUrl: './dashboard.html'
})
export class DashboardAdmin implements OnInit {

  // KPIs
  activeActivitiesCount = 0;
  inactiveActivitiesCount = 0;
  totalRevenue = 0;
  totalRegistrations = 0;

  // Charts
  // Charts
revenueByMonthChartData: any = { labels: [], datasets: [] };
registrationsByActivityChartData: any = { labels: [], datasets: [] };
revenueByActivityChartData: any = { labels: [], datasets: [] };
registrationStatusChartData: any = { labels: [], datasets: [] };
sessionsByAnimatorChartData: any = { labels: [], datasets: [] };

chartOptions: any = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true
    }
  }
};

  constructor(private adminStatsService: AdminStatsService) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {

    // 📊 Inscriptions par activité
    this.adminStatsService.getRegistrationsByActivity().subscribe(data => {
      this.totalRegistrations = data.reduce((sum, d) => sum + d.registrationsCount, 0);

      this.registrationsByActivityChartData = {
        labels: data.map(d => d.activityName),
        datasets: [{
          data: data.map(d => d.registrationsCount),
          label: 'Inscriptions'
        }]
      };
    });

    // 📈 Revenus par mois
    this.adminStatsService.getRevenueByMonth().subscribe(data => {
      this.totalRevenue = data.reduce((sum, d) => sum + d.revenue, 0);

      this.revenueByMonthChartData = {
        labels: data.map(d => `${d.month}/${d.year}`),
        datasets: [{
          data: data.map(d => d.revenue),
          label: 'Revenus ($)'
        }]
      };
    });

    // 💰 Revenus par activité
    this.adminStatsService.getRevenueByActivity().subscribe(data => {
      this.revenueByActivityChartData = {
        labels: data.map(d => d.activityName),
        datasets: [{
          data: data.map(d => d.revenue),
          label: 'Revenus ($)'
        }]
      };
    });

    // 🟢 Statuts
    this.adminStatsService.getRegistrationsByStatus().subscribe(data => {
      this.registrationStatusChartData = {
        labels: data.map(d => d.status),
        datasets: [{
          data: data.map(d => d.count)
        }]
      };
    });

    // 🧑‍🏫 Animateurs
    this.adminStatsService.getSessionsByAnimator().subscribe(data => {
      this.sessionsByAnimatorChartData = {
        labels: data.map(d => d.animatorName),
        datasets: [{
          data: data.map(d => d.sessionsCount),
          label: 'Sessions'
        }]
      };
    });

    // KPIs
    this.adminStatsService.getActiveActivitiesCount().subscribe(c => this.activeActivitiesCount = c);
    this.adminStatsService.getInactiveActivitiesCount().subscribe(c => this.inactiveActivitiesCount = c);
  }
}