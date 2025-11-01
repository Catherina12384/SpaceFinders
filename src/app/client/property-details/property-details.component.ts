import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatStepperModule } from '@angular/material/stepper';

// ============================================================================
// INTERFACES
// ============================================================================
interface PropertyDetails {
  propertyId: number;
  propertyName: string;
  propertyDescription: string;
  noOfRooms: number;
  noOfBathrooms: number;
  maxNoOfGuests: number;
  pricePerDay: number;
  imageURL: string;
  buildingNo: string;
  street: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  hostId: number;
  hostName: string;
  hostPhone: string;
  propertyStatus: string;
  propertyRate: number;
  propertyRatingCount: number;
  hasWifi: boolean;
  hasParking: boolean;
  hasPool: boolean;
  hasAc: boolean;
  hasHeater: boolean;
  hasPetFriendly: boolean;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// ============================================================================
// COMPONENT
// ============================================================================
@Component({
  selector: 'app-property-details',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    // Material
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatStepperModule
  ],
  templateUrl: './property-details.component.html',
  styleUrls: ['./property-details.component.css']
})
export class PropertyDetailsComponent implements OnInit {

  // --------------------------------------------------------------------------
  // STATE
  // --------------------------------------------------------------------------
  private readonly API_BASE_URL = 'http://localhost:8080/v1/api';

  property: PropertyDetails | null = null;
  propertyId: number | null = null;

  // stepper state (0 = Dates, 1 = Services, 2 = Confirm)
  currentStep = 0;
  showBookingForm = false;

  // UI flags
  isLoading = false;
  isBooking = false;

  // messages
  errorMessage = '';
  successMessage = '';

  // form
  bookingForm!: FormGroup;

  // date limits
  minDate = new Date();
  maxDate = new Date();

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {
    // allow up to one year ahead
    this.maxDate.setFullYear(this.maxDate.getFullYear() + 1);
  }

  // --------------------------------------------------------------------------
  // LIFECYCLE
  // --------------------------------------------------------------------------
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.propertyId = parseInt(id, 10);
        this.loadPropertyDetails();
      } else {
        this.showError('Invalid property ID');
        this.router.navigate(['/client/search-properties']);
      }
    });

    this.initForm();
  }

  // --------------------------------------------------------------------------
  // FORM
  // --------------------------------------------------------------------------
  private initForm(): void {
    this.bookingForm = this.fb.group({
      checkinDate: [null, [Validators.required]],
      checkoutDate: [null, [Validators.required]],
      hasExtraCot: [false],
      hasDeepClean: [false]
    }, { validators: this.dateRangeValidator });
  }

  private dateRangeValidator(form: FormGroup) {
    const cin = form.get('checkinDate')?.value;
    const cout = form.get('checkoutDate')?.value;
    if (!cin || !cout) return null;

    const checkin = new Date(cin);
    const checkout = new Date(cout);

    if (checkout <= checkin) return { invalidDateRange: true };

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (checkin < today) return { pastDate: true };

    return null;
  }

  // --------------------------------------------------------------------------
  // API
  // --------------------------------------------------------------------------
  loadPropertyDetails(): void {
    if (!this.propertyId) return;

    this.isLoading = true;
    this.errorMessage = '';

    const url = `${this.API_BASE_URL}/client/viewClickedProperty/${this.propertyId}`;
    this.http.get<ApiResponse<PropertyDetails>>(url).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.property = res.data;
          this.showSuccess('Property details loaded successfully');
        } else {
          this.showError('Failed to load property details');
        }
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.handleError('Failed to load property details', err);
        this.isLoading = false;
      }
    });
  }

  // --------------------------------------------------------------------------
  // BOOKING FLOW
  // --------------------------------------------------------------------------
  openBookingForm(): void {
    this.showBookingForm = true;
    this.bookingForm.reset({
      checkinDate: null,
      checkoutDate: null,
      hasExtraCot: false,
      hasDeepClean: false
    });
    this.currentStep = 0;
    this.errorMessage = '';
  }

  closeBookingForm(): void {
    this.showBookingForm = false;
    this.bookingForm.reset({
      checkinDate: null,
      checkoutDate: null,
      hasExtraCot: false,
      hasDeepClean: false
    });
    this.currentStep = 0;
    this.errorMessage = '';
  }

  previousStep(): void {
    if (this.currentStep > 0) this.currentStep--;
  }

  nextStep(): void {
    // Step 0: validate dates before moving to Services
    if (this.currentStep === 0) {
      const cin = this.bookingForm.get('checkinDate')?.value;
      const cout = this.bookingForm.get('checkoutDate')?.value;

      if (!cin || !cout) {
        this.showError('Please select both check-in and check-out dates');
        this.bookingForm.get('checkinDate')?.markAsTouched();
        this.bookingForm.get('checkoutDate')?.markAsTouched();
        return;
      }
      if (this.bookingForm.errors?.['invalidDateRange']) {
        this.showError('Check-out date must be after check-in date');
        return;
      }
      if (this.bookingForm.errors?.['pastDate']) {
        this.showError('Check-in date cannot be in the past');
        return;
      }

      this.currentStep = 1;  // → Additional Services
      return;
    }

    // Step 1: move to Confirmation
    if (this.currentStep === 1) {
      this.currentStep = 2;
      return;
    }

    // Step 2: proceed to payment
    if (this.currentStep === 2) {
      this.submitBooking();
    }
  }

  submitBooking(): void {
    if (this.bookingForm.invalid || !this.property) {
      this.markFormGroupTouched(this.bookingForm);
      return;
    }

    const userIdStr = sessionStorage.getItem('userId');
    if (!userIdStr) {
      this.showError('Please login first');
      this.router.navigate(['/home']);
      return;
    }

    const userId = parseInt(userIdStr, 10);
    const fv = this.bookingForm.value;

    const checkinDate = fv.checkinDate.toISOString().split('T')[0];
    const checkoutDate = fv.checkoutDate.toISOString().split('T')[0];
    const nights = this.calculateNights(checkinDate, checkoutDate);
    const totalAmount = this.calculateTotalPrice(this.property.pricePerDay, nights);

    const paymentData = {
      propertyId: this.property.propertyId,
      propertyName: this.property.propertyName,
      propertyImage: this.property.imageURL,
      propertyLocation: `${this.property.city}, ${this.property.state}`,
      pricePerNight: this.property.pricePerDay,
      bookingId: null,
      checkinDate,
      checkoutDate,
      numberOfNights: nights,
      maxGuests: this.property.maxNoOfGuests,
      hasExtraCot: Boolean(fv.hasExtraCot),
      hasDeepClean: Boolean(fv.hasDeepClean),
      totalAmount,
      userId
    };

    // Optional: call a service to flag add-ons (your existing PoC endpoint)
    this.bookRoomService(paymentData);

    // Navigate to payment page with state
    this.router.navigate(['/client/payment-page'], { state: { paymentData } });
  }
  goBack(): void {
    this.router.navigate(['/client/search-properties']);
  }

  // ==========================================================================
  // UTILITY METHODS
  // ==========================================================================

  private bookRoomService(paymentData: any) {
    const url = `${this.API_BASE_URL}/client/alwaysTrue`;

    // Only call when deep clean is chosen (kept from your code)
    if (paymentData.hasDeepClean) {
      this.http.post<boolean>(url, paymentData).subscribe({
        next: (ok) => {
          paymentData.hasDeepClean = !!ok;
          if (ok) this.showSuccess('Room service added successfully!');
          else this.showError('Room service failed successfully!');
        },
        error: (err: HttpErrorResponse) => {
          paymentData.hasDeepClean = false;
          this.handleError('Payment processing failed', err);
        }
      });
    }
  }

  // --------------------------------------------------------------------------
  // UTILS
  // --------------------------------------------------------------------------
  getRatingStars(rating: number): string {
    const full = Math.floor(rating);
    const half = rating % 1 >= 0.5 ? 1 : 0;
    const empty = 5 - full - half;
    return '★'.repeat(full) + (half ? '½' : '') + '☆'.repeat(empty);
  }

  formatPrice(price: number): string {
    return `₹${price.toLocaleString()}/night`;
  }

  calculateNights(checkinDate: string, checkoutDate: string): number {
    const cin = new Date(checkinDate);
    const cout = new Date(checkoutDate);
    const diff = cout.getTime() - cin.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  calculateTotalPrice(pricePerDay: number, nights: number): number {
    return pricePerDay * nights;
  }

  formatDate(dateString: string | null): string {
    if (!dateString) return '';
    try {
      const d = new Date(dateString);
      return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    } catch {
      return dateString;
    }
  }

  getDefaultImage(): string {
    return 'assets/images/download.jpg';
  }

  handleImageError(event: Event): void {
    (event.target as HTMLImageElement).src = this.getDefaultImage();
  }

  getErrorMessage(fieldName: string): string {
    const c = this.bookingForm.get(fieldName);
    if (c && c.touched && c.errors?.['required']) {
      if (fieldName === 'checkinDate') return 'Check-in Date is required.';
      if (fieldName === 'checkoutDate') return 'Check-out Date is required.';
      return 'This field is required.';
    }
    if (this.bookingForm.errors?.['invalidDateRange']) return 'Check-out date must be after check-in date.';
    if (this.bookingForm.errors?.['pastDate']) return 'Check-in date cannot be in the past.';
    return '';
  }

  hasError(fieldName: string): boolean {
    const c = this.bookingForm.get(fieldName);
    return !!(c && c.invalid && c.touched);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      if (control instanceof FormGroup) this.markFormGroupTouched(control);
    });
  }

  // --------------------------------------------------------------------------
  // ERROR HANDLING
  // --------------------------------------------------------------------------
  private handleError(context: string, error: HttpErrorResponse): void {
    console.error(`Error ${context}:`, error);

    if (error.status === 0) {
      this.errorMessage = 'Cannot connect to server. Please check if the backend is running.';
    } else if (error.status === 401) {
      this.errorMessage = 'Unauthorized. Please login again.';
      setTimeout(() => this.router.navigate(['/home']), 2000);
    } else if (error.status === 404) {
      this.errorMessage = 'Property not found.';
      setTimeout(() => this.router.navigate(['/client/search-properties']), 2000);
    } else if (error.status >= 500) {
      this.errorMessage = 'Server error. Please try again later.';
    } else {
      this.errorMessage = (error.error?.message as string) || 'An unexpected error occurred.';
    }

    this.showError(this.errorMessage);
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => (this.errorMessage = ''), 5000);
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => (this.successMessage = ''), 3000);
  }
}
