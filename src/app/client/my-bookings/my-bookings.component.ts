import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

// ============================================================================
// INTERFACES
// ============================================================================

interface BookingResponse {
  bookingId: number;
  propertyId: number;
  propertyName: string;
  propertyImage: string;
  city: string;
  userId: number;
  username: string;
  checkinDate: string;
  checkoutDate: string;
  isPaymentStatus: boolean;
  isBookingStatus: string;
  hasExtraCot: boolean;
  hasDeepClean: boolean;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

interface ModifyBookingRequest {
  bookingId: number;
  checkinDate: string;
  checkoutDate: string;
  hasExtraCot: boolean;
  hasDeepClean: boolean;
}

// ============================================================================
// COMPONENT
// ============================================================================

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatChipsModule,
    MatMenuModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './my-bookings.component.html',
  styleUrls: ['./my-bookings.component.css']
})
export class MyBookingsComponent implements OnInit {
  
  // ==========================================================================
  // PROPERTIES
  // ==========================================================================
  
  private readonly API_BASE_URL = 'http://localhost:8080/v1/api';
  
  // Data
  allBookings: BookingResponse[] = [];
  upcomingBookings: BookingResponse[] = [];
  pastBookings: BookingResponse[] = [];
  currentBookings: BookingResponse[] = [];
  
  // UI state
  isLoading = false;
  isCancelling = false;
  isModifying = false;
  activeTab = 'upcoming';
  selectedBooking: BookingResponse | null = null;
  showBookingDetails = false;
  showModifyDialog = false;
  
  // Messages
  errorMessage = '';
  successMessage = '';
  
  // Forms
  modifyForm!: FormGroup;
  
  // Current date for comparison
  currentDate: Date = new Date();
  minDate = new Date();
  maxDate = new Date();
  
  // ==========================================================================
  // CONSTRUCTOR
  // ==========================================================================
  
  constructor(
    private http: HttpClient,
    public router: Router,
    private fb: FormBuilder
  ) {
    this.maxDate.setFullYear(this.maxDate.getFullYear() + 1);
  }
  
  // ==========================================================================
  // LIFECYCLE HOOKS
  // ==========================================================================
  
  ngOnInit(): void {
    this.initializeModifyForm();
    this.loadBookings();
  }
  
  // ==========================================================================
  // FORM INITIALIZATION
  // ==========================================================================
  
  private initializeModifyForm(): void {
    this.modifyForm = this.fb.group({
      checkinDate: ['', [Validators.required]],
      checkoutDate: ['', [Validators.required]],
      hasExtraCot: [false],
      hasDeepClean: [false]
    }, { validators: this.dateRangeValidator });
  }
  
  private dateRangeValidator(form: FormGroup) {
    const checkin = form.get('checkinDate')?.value;
    const checkout = form.get('checkoutDate')?.value;
    
    if (checkin && checkout) {
      const checkinDate = new Date(checkin);
      const checkoutDate = new Date(checkout);
      
      if (checkoutDate <= checkinDate) {
        return { invalidDateRange: true };
      }
      
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      
      if (checkinDate < today) {
        return { pastDate: true };
      }
    }
    
    return null;
  }
  
  // ==========================================================================
  // API CALLS
  // ==========================================================================
  
  loadBookings(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    const userIdStr = sessionStorage.getItem('userId');
    if (!userIdStr) {
      this.showError('Please login first');
      this.router.navigate(['/home']);
      return;
    }
    
    const userId = parseInt(userIdStr, 10);
    const url = `${this.API_BASE_URL}/client/viewBooking/${userId}`;
    
    this.http.get<ApiResponse<BookingResponse[]>>(url).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.allBookings = response.data;
          this.processBookings();
          this.showSuccess('Bookings loaded successfully');
        } else {
          this.showError('Failed to load bookings');
        }
        this.isLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Failed to load bookings', error);
        this.isLoading = false;
      }
    });
  }
  
  // ==========================================================================
  // CANCEL BOOKING IMPLEMENTATION
  // ==========================================================================
  
  cancelBooking(booking: BookingResponse): void {
    if (!confirm(`Are you sure you want to cancel the booking for "${booking.propertyName}"?\n\nThis action cannot be undone.`)) {
      return;
    }
    
    this.isCancelling = true;
    this.errorMessage = '';
    
    const url = `${this.API_BASE_URL}/client/cancelBooking/${booking.bookingId}`;
    
    this.http.delete<ApiResponse<any>>(url, {}).subscribe({
      next: (response) => {
        if (response.success) {
          this.showSuccess('Booking cancelled successfully');
          this.loadBookings(); // Reload bookings to reflect changes
        } else {
          this.showError(response.message || 'Failed to cancel booking');
        }
        this.isCancelling = false;
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Failed to cancel booking', error);
        this.isCancelling = false;
      }
    });
  }
  
  // ==========================================================================
  // MODIFY BOOKING IMPLEMENTATION
  // ==========================================================================
  
  modifyBooking(booking: BookingResponse): void {
    this.selectedBooking = booking;
    this.showModifyDialog = true;
    
    // Pre-fill form with current booking data
    this.modifyForm.patchValue({
      checkinDate: new Date(booking.checkinDate),
      checkoutDate: new Date(booking.checkoutDate),
      hasExtraCot: booking.hasExtraCot,
      hasDeepClean: booking.hasDeepClean
    });
  }
  
  closeModifyDialog(): void {
    this.showModifyDialog = false;
    this.selectedBooking = null;
    this.modifyForm.reset();
    this.errorMessage = '';
  }
  
  submitModifyBooking(): void {
    if (this.modifyForm.invalid || !this.selectedBooking) {
      this.markFormGroupTouched(this.modifyForm);
      return;
    }
    
    this.isModifying = true;
    this.errorMessage = '';
    
    const modifyRequest: ModifyBookingRequest = {
      bookingId: this.selectedBooking.bookingId,
      checkinDate: this.formatDateForAPI(this.modifyForm.value.checkinDate),
      checkoutDate: this.formatDateForAPI(this.modifyForm.value.checkoutDate),
      hasExtraCot: this.modifyForm.value.hasExtraCot,
      hasDeepClean: this.modifyForm.value.hasDeepClean
    };
    
    // Note: Using a placeholder URL - update with actual modify booking endpoint when available
    const url = `${this.API_BASE_URL}/client/modifyBooking`;
    
    this.http.put<ApiResponse<any>>(url, modifyRequest).subscribe({
      next: (response) => {
        if (response.success) {
          this.showSuccess('Booking modified successfully');
          this.closeModifyDialog();
          this.loadBookings();
        } else {
          this.showError(response.message || 'Failed to modify booking');
        }
        this.isModifying = false;
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Failed to modify booking', error);
        this.isModifying = false;
      }
    });
  }
  
  private formatDateForAPI(date: Date): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toISOString().split('T')[0];
  }
  
  // ==========================================================================
  // ADD REVIEW IMPLEMENTATION (PLACEHOLDER)
  // ==========================================================================
  
  addReview(booking: BookingResponse): void {
    // Navigate to review page or open review dialog
    // This can be implemented based on your requirements
    this.router.navigate(['/client/add-review', booking.bookingId]);
  }
  
  // ==========================================================================
  // DATA PROCESSING
  // ==========================================================================
  
  private processBookings(): void {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    this.upcomingBookings = [];
    this.pastBookings = [];
    this.currentBookings = [];
    
    this.allBookings.forEach((booking) => {
      const checkinDate = new Date(booking.checkinDate);
      const checkoutDate = new Date(booking.checkoutDate);
      checkinDate.setHours(0, 0, 0, 0);
      checkoutDate.setHours(0, 0, 0, 0);
      
      if (booking.isBookingStatus === 'CONFIRMED' || booking.isBookingStatus === 'PENDING') {
        if (checkinDate > today) {
          this.upcomingBookings.push(booking);
        } else if (checkinDate <= today && checkoutDate >= today) {
          this.currentBookings.push(booking);
        } else {
          this.pastBookings.push(booking);
        }
      } else {
        this.pastBookings.push(booking);
      }
    });
    
    this.upcomingBookings.sort((a, b) => 
      new Date(a.checkinDate).getTime() - new Date(b.checkinDate).getTime()
    );
    
    this.pastBookings.sort((a, b) => 
      new Date(b.checkinDate).getTime() - new Date(a.checkinDate).getTime()
    );
    
    this.currentBookings.sort((a, b) => 
      new Date(a.checkinDate).getTime() - new Date(b.checkinDate).getTime()
    );
  }
  
  // ==========================================================================
  // TAB MANAGEMENT
  // ==========================================================================
  
  onTabChange(event: any): void {
    this.activeTab = event.index === 0 ? 'upcoming' : 
                    event.index === 1 ? 'current' : 'past';
  }
  
  getBookingsForCurrentTab(): BookingResponse[] {
    switch (this.activeTab) {
      case 'upcoming':
        return this.upcomingBookings;
      case 'current':
        return this.currentBookings;
      case 'past':
        return this.pastBookings;
      default:
        return [];
    }
  }
  
  // ==========================================================================
  // BOOKING ACTIONS
  // ==========================================================================
  
  viewBookingDetails(booking: BookingResponse): void {
    this.router.navigate(['/client/booking-details', booking.bookingId]);
  }
  
  closeBookingDetails(): void {
    this.selectedBooking = null;
    this.showBookingDetails = false;
  }
  
  // ==========================================================================
  // NAVIGATION
  // ==========================================================================
  
  goBack(): void {
    this.router.navigate(['/client/dashboard']);
  }
  
  viewPropertyDetails(propertyId: number): void {
    this.router.navigate(['/client/property-details', propertyId]);
  }
  
  // ==========================================================================
  // UTILITY METHODS
  // ==========================================================================
  
  getBookingStatusClass(status: string): string {
    const statusClasses: { [key: string]: string } = {
      'CONFIRMED': 'status-confirmed',
      'PENDING': 'status-pending',
      'CANCELLED': 'status-cancelled',
      'COMPLETED': 'status-completed'
    };
    return statusClasses[status] || 'status-default';
  }
  
  getBookingStatusIcon(status: string): string {
    const statusIcons: { [key: string]: string } = {
      'CONFIRMED': 'check_circle',
      'PENDING': 'schedule',
      'CANCELLED': 'cancel',
      'COMPLETED': 'done_all'
    };
    return statusIcons[status] || 'help';
  }
  
  getPaymentStatusClass(isPaid: boolean): string {
    return isPaid ? 'payment-paid' : 'payment-pending';
  }
  
  getPaymentStatusText(isPaid: boolean): string {
    return isPaid ? 'Paid' : 'Pending';
  }
  
  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch (error) {
      console.error('Error formatting date:', error);
      return dateString;
    }
  }
  
  formatDateTime(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      console.error('Error formatting datetime:', error);
      return dateString;
    }
  }
  
  getDefaultImage(): string {
    return 'assets/images/download.jpg';
  }
  
  handleImageError(event: Event): void {
    console.log('Image load error, using default image');
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = this.getDefaultImage();
  }
  
  calculateNights(checkinDate: string, checkoutDate: string): number {
    const checkin = new Date(checkinDate);
    const checkout = new Date(checkoutDate);
    const diffTime = checkout.getTime() - checkin.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }
  
  isBookingActionable(booking: BookingResponse): boolean {
    const today = new Date();
    const checkinDate = new Date(booking.checkinDate);
    const checkoutDate = new Date(booking.checkoutDate);
    
    return booking.isBookingStatus === 'CONFIRMED' && 
           checkinDate > today && 
           checkoutDate > today;
  }
  
  hasError(fieldName: string): boolean {
    const control = this.modifyForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }
  
  getErrorMessage(fieldName: string): string {
    const control = this.modifyForm.get(fieldName);
    
    if (control && control.touched && control.errors) {
      if (control.errors['required']) {
        return `${this.capitalizeFirstLetter(fieldName)} is required.`;
      }
    }
    
    const formErrors = this.modifyForm.errors;
    if (formErrors) {
      if (formErrors['invalidDateRange']) {
        return 'Check-out date must be after check-in date.';
      }
      if (formErrors['pastDate']) {
        return 'Check-in date cannot be in the past.';
      }
    }
    
    return '';
  }
  
  private capitalizeFirstLetter(text: string): string {
    if (text === 'checkinDate') return 'Check-in Date';
    if (text === 'checkoutDate') return 'Check-out Date';
    return text.charAt(0).toUpperCase() + text.slice(1);
  }
  
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
  
  // ==========================================================================
  // ERROR HANDLING
  // ==========================================================================
  
  private handleError(context: string, error: HttpErrorResponse): void {
    console.error(`Error ${context}:`, error);
    
    if (error.status === 0) {
      this.errorMessage = 'Cannot connect to server. Please check if the backend is running.';
    } else if (error.status === 401) {
      this.errorMessage = 'Unauthorized. Please login again.';
      setTimeout(() => this.router.navigate(['/home']), 2000);
    } else if (error.status === 404) {
      this.errorMessage = 'No bookings found.';
    } else if (error.status >= 500) {
      this.errorMessage = 'Server error. Please try again later.';
    } else {
      this.errorMessage = error.error?.message || 'An unexpected error occurred.';
    }
    
    this.showError(this.errorMessage);
  }
  
  private showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => this.errorMessage = '', 5000);
  }
  
  private showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => this.successMessage = '', 3000);
  }
}
