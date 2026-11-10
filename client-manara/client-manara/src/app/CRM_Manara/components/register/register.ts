import { RegisterPayload } from '../../models/register-payload-form';
import { AfterViewInit, Component,  OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
declare var $: any;
@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent implements AfterViewInit,  OnInit{

  form = {
    nom: '',
    prenom: '',
    date: '',
    email: '',
    phone: '',
    password: '',
    confirm: ''
  };

  alertMessage = '';
  alertType: 'success' | 'error' = 'success';
  isLoading = false;


  maxBirthDate = '';
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  ngOnInit(): void { 
    const today = new Date();

today.setFullYear(today.getFullYear() - 21);

this.maxBirthDate = today.toISOString().split('T')[0];

  }

  
   
  onSubmit(): void {
    this.alertMessage = '';

    if (
      !this.form.nom.trim() ||
      !this.form.prenom.trim() ||
      !this.form.email.trim() ||
      !this.form.phone.trim() ||
      !this.form.password.trim() ||
      !this.form.confirm.trim()
    ) {
      this.showMessage('Veuillez remplir tous les champs obligatoires.', 'error');
      return;
    }

    if (this.form.password !== this.form.confirm) {
      this.showMessage('Les mots de passe ne correspondent pas.', 'error');
      return;
    }

    if (!this.isStrongPassword()) {
  this.showMessage(
    'Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un symbole.',
    'error'
  );
  return;
}
const cleanPhone = this.form.phone.trim().replace(/\D/g, '');
const finalPhone = cleanPhone;
  const birthDate = new Date(this.form.date);
const today = new Date();

let age = today.getFullYear() - birthDate.getFullYear();

const monthDifference = today.getMonth() - birthDate.getMonth();

if (
  monthDifference < 0 ||
  (monthDifference === 0 && today.getDate() < birthDate.getDate())
) {
  age--;
}

if (age < 21) {
  this.showMessage(
    'Vous devez avoir au moins 21 ans pour créer un compte.',
    'error'
  );
  return;
}

    const payload: RegisterPayload = {
  firstName: this.form.prenom.trim(),
  lastName: this.form.nom.trim(),
  date: this.form.date,
  email: this.form.email.trim().toLowerCase(),
  phone: finalPhone,
  password: this.form.password
};

    this.isLoading = true;

    this.authService.register(payload).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.showMessage(response?.message || 'Compte créé. Vérifiez votre email.', 'success');

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1200);
      },
      error: (error) => {
        this.isLoading = false;
        this.showMessage(
          error?.error?.message || 'Erreur réseau ou serveur inaccessible.',
          'error'
        );
      }
    });
  }

  private showMessage(message: string, type: 'success' | 'error'): void {
    this.alertMessage = message;
    this.alertType = type;
  }
isValidEmail(): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email.trim());
}
isStrongPassword(): boolean {
  return /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(this.form.password);
}
  ngAfterViewInit(): void {
      if ($('#registerForm').data('validator')) {
    $('#registerForm').removeData('validator');
    $('#registerForm').off('.validate');
  }
    $.validator.addMethod(
  'strongPassword',
  function(value: string) {
    return /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(value);
  },
  'Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un symbole.'
);


  $('#registerForm').validate({

    rules: {
      nom: {
        required: true,
        minlength: 2
      },

      prenom: {
        required: true,
        minlength: 2
      },

      email: {
        required: true,
        email: true
      },

      phone: {
        required: true,
  digits: true,
  minlength: 10
      },

      password: {
      required: true,
  minlength: 8,
  strongPassword: true
      },

      confirm: {
        required: true,
        equalTo: '#password'
      }
    },

    messages: {
      nom: {
        required: 'Nom requis'
      },
phone: {
  required: 'Téléphone requis',
  digits: 'Entrez uniquement des chiffres',
  minlength: 'Au moins 10 chiffres'
},
      prenom: {
        required: 'Prénom requis'
      },

      email: {
        required: 'Email requis',
        email: 'Email invalide'
      },

      password: {
  required: 'Mot de passe requis',
  minlength: '8 caractères minimum',
  strongPassword: 'Le mot de passe doit contenir une majuscule, une minuscule, un chiffre et un symbole.'
      },

      confirm: {
        equalTo: 'Les mots de passe ne correspondent pas'
      }
    },

    errorClass: 'text-red-200 text-sm mt-1 block',

    highlight: function(element: any) {

      $(element)
        .removeClass('border-green-400')
        .addClass('border-2 border-red-400');

    },

    unhighlight: function(element: any) {

      $(element)
        .removeClass('border-red-400')
        .addClass('border-2 border-green-400');

    }

  });

}
formatPhone(): void {

  let value = this.form.phone.replace(/\D/g, '');

  // max 10 chiffres
  value = value.substring(0, 11);

  if (value.length > 6) {

    this.form.phone =
      value.substring(0, 3) +
      value.substring(3, 6) +
      value.substring(6);

  } else if (value.length > 3) {

    this.form.phone =
      value.substring(0, 3) +
      value.substring(3);

  } else {

    this.form.phone = value;

  }

}
}