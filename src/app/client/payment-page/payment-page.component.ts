import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';

// ============================================================================
// INTERFACES
// ============================================================================

interface PaymentData {
  // Property Information
  propertyId: number;
  propertyName: string;
  propertyImage: string;
  propertyLocation: string;
  pricePerNight: number;

  // Booking Information
  bookingId: number | null;
  checkinDate: string;
  checkoutDate: string;
  numberOfNights: number;
  maxGuests: number;

  // Additional Services
  hasExtraCot: boolean;
  hasDeepClean: boolean;

  // Pricing
  totalAmount: number;

  // User Information
  userId: number;
}

interface PaymentRequest {
  bookingId?: number;
  userId: number;
  accountNumber: string;
  amount: number;
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
  selector: 'app-payment-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './payment-page.component.html',
  styleUrl: './payment-page.component.css'
})
export class PaymentPageComponent implements OnInit {

  // ==========================================================================
  // PROPERTIES
  // ==========================================================================

  private readonly API_BASE_URL = 'http://localhost:8080/v1/api';

  // Payment Data
  paymentData: PaymentData | null = null;

  // Form Data
  accountNumber: string = '';

  // UI State
  isProcessing = false;
  errorMessage = '';
  successMessage = '';

  // ==========================================================================
  // CONSTRUCTOR
  // ==========================================================================

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Get navigation state
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      this.paymentData = navigation.extras.state['paymentData'];
    }
  }

  // ==========================================================================
  // LIFECYCLE HOOKS
  // ==========================================================================

  ngOnInit(): void {
    // Check if payment data exists
    if (!this.paymentData) {
      this.showError('No booking data found. Redirecting...');
      setTimeout(() => {
        this.router.navigate(['/client/search-properties']);
      }, 2000);
    }
  }

  // ==========================================================================
  // API CALLS
  // ==========================================================================

  processPayment(): void {
    // Validate account number
    if (!this.accountNumber || this.accountNumber.trim() === '') {
      this.showError('Please enter your account number');
      return;
    }

    if (!this.paymentData) {
      this.showError('Payment data is missing');
      return;
    }

    this.isProcessing = true;
    this.errorMessage = '';

    const paymentRequest: PaymentRequest = {
      bookingId: this.paymentData.bookingId || undefined,
      userId: this.paymentData.userId,
      accountNumber: this.accountNumber.trim(),
      amount: this.paymentData.totalAmount
    };

    const url = `${this.API_BASE_URL}/client/alwaysTrue`; // Update with your actual endpoint ------------------------------------------------------

    this.http.post<boolean>(url, paymentRequest).subscribe({
      next: (isSuccessful: boolean) => {
        console.log("Payment: ",isSuccessful);
        if (isSuccessful) {
          this.showSuccess('Payment processed successfully!');
          this.accountNumber = '';

          // ✅ Proceed to make booking after successful payment
          this.makeBooking();
        } else {
          this.showError(isSuccessful || 'Payment processing failed');
        }
        this.isProcessing = false;
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Payment processing failed', error);
        this.isProcessing = false;
      }
    });
  }

  private makeBooking(): void {
    if (!this.paymentData) {
      this.showError('Missing booking data for confirmation.');
      return;
    }

    const bookingRequest = {
      propertyId: this.paymentData.propertyId,
      userId: this.paymentData.userId,
      checkinDate: this.paymentData.checkinDate,
      checkoutDate: this.paymentData.checkoutDate,
      hasExtraCot: this.paymentData.hasExtraCot,
      hasDeepClean: this.paymentData.hasDeepClean
    };

    const url = `${this.API_BASE_URL}/client/makeBooking`;

    this.http.post<ApiResponse<any>>(url, bookingRequest).subscribe({
      next: (response) => {
        if (response.success) {
          this.showSuccess('Booking confirmed successfully!');
          setTimeout(() => {
            this.router.navigate(['/client/my-bookings']);
          }, 2000);
        } else {
          this.showError('Booking confirmation failed.');
        }
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Failed to confirm booking', error);
      }
    });
  }

  // ==========================================================================
  // UTILITY METHODS
  // ==========================================================================

  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      });
    } catch (error) {
      console.error('Error formatting date:', error);
      return dateString;
    }
  }

  formatPrice(price: number): string {
    return `₹${price.toLocaleString()}`;
  }

  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'assets/images/download.jpg';
  }

  goBack(): void {
    this.router.navigate(['/client/search-properties']);
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
    } else if (error.status >= 500) {
      this.errorMessage = 'Server error. Please try again later.';
    } else {
      this.errorMessage = error.error?.message || 'An unexpected error occurred.';
    }
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => this.errorMessage = '', 5000);
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
  }
}

