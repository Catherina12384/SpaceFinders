import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';

// ============================================================================
// INTERFACES
// ============================================================================

interface ReviewRequest {
  bookingId: number;
  rating: number;
}

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

// ============================================================================
// COMPONENT
// ============================================================================

@Component({
  selector: 'app-add-review',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSliderModule
  ],
  templateUrl: './add-review.component.html',
  styleUrls: ['./add-review.component.css']
})
export class AddReviewComponent implements OnInit {
  
  // ==========================================================================
  // PROPERTIES
  // ==========================================================================
  
  private readonly API_BASE_URL = 'http://localhost:8080/v1/api';
  
  // Data
  bookingId: number | null = null;
  booking: BookingResponse | null = null;
  
  // UI state
  isLoading = false;
  isSubmitting = false;
  
  // Messages
  errorMessage = '';
  successMessage = '';
  
  // Forms
  reviewForm!: FormGroup;
  
  // Rating display
  ratingValue = 0;
  hoveredRating = 0;
  
  // ==========================================================================
  // CONSTRUCTOR
  // ==========================================================================
  
  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {}
  
  // ==========================================================================
  // LIFECYCLE HOOKS
  // ==========================================================================
  
  ngOnInit(): void {
    this.initializeReviewForm();
    
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.bookingId = parseInt(id, 10);
        this.loadBookingDetails();
      } else {
        this.showError('Invalid booking ID');
        this.router.navigate(['/client/my-bookings']);
      }
    });
  }
  
  // ==========================================================================
  // FORM INITIALIZATION
  // ==========================================================================
  
  private initializeReviewForm(): void {
    this.reviewForm = this.fb.group({
      rating: [0, [Validators.required, Validators.min(0.5), Validators.max(5)]]
    });
  }
  
  // ==========================================================================
  // API CALLS
  // ==========================================================================
  
  loadBookingDetails(): void {
    if (!this.bookingId) return;
    
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
          const booking = response.data.find(b => b.bookingId === this.bookingId);
          if (booking) {
            this.booking = booking;
            
            // Check if booking is eligible for review (completed status)
            if (booking.isBookingStatus !== 'COMPLETED') {
              this.showError('Only completed bookings can be reviewed');
              setTimeout(() => this.router.navigate(['/client/my-bookings']), 2000);
            }
          } else {
            this.showError('Booking not found');
            this.router.navigate(['/client/my-bookings']);
          }
        } else {
          this.showError('Failed to load booking details');
        }
        this.isLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Failed to load booking details', error);
        this.isLoading = false;
      }
    });
  }
  
  submitReview(): void {
    if (this.reviewForm.invalid || !this.bookingId) {
      this.markFormGroupTouched(this.reviewForm);
      return;
    }
    
    if (this.ratingValue === 0) {
      this.showError('Please select a rating');
      return;
    }
    
    this.isSubmitting = true;
    this.errorMessage = '';
    
    const reviewRequest: ReviewRequest = {
      bookingId: this.bookingId,
      rating: this.ratingValue
    };
    
    const url = `${this.API_BASE_URL}/admin/closeBookingAndRating`;
    
    this.http.post<ApiResponse<any>>(url, reviewRequest).subscribe({
      next: (response) => {
        if (response.success) {
          this.showSuccess('Review submitted successfully! Thank you for your feedback.');
          this.reviewForm.reset();
          this.ratingValue = 0;
          
          // Redirect to bookings after a delay
          setTimeout(() => {
            this.router.navigate(['/client/my-bookings']);
          }, 2000);
        } else {
          this.showError(response.message || 'Failed to submit review');
        }
        this.isSubmitting = false;
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Failed to submit review', error);
        this.isSubmitting = false;
      }
    });
  }
  
  // ==========================================================================
  // RATING METHODS
  // ==========================================================================
  
  setRating(rating: number): void {
    this.ratingValue = rating;
    this.reviewForm.patchValue({ rating: rating });
  }
  
  setHoveredRating(rating: number): void {
    this.hoveredRating = rating;
  }
  
  clearHoveredRating(): void {
    this.hoveredRating = 0;
  }
  
  getStarIcon(position: number): string {
    const displayRating = this.hoveredRating || this.ratingValue;
    
    if (position <= displayRating) {
      return 'star';
    } else if (position - 0.5 === displayRating) {
      return 'star_half';
    } else {
      return 'star_border';
    }
  }
  
  getStarClass(position: number): string {
    const displayRating = this.hoveredRating || this.ratingValue;
    return position <= displayRating ? 'star-filled' : 'star-empty';
  }
  
  getRatingText(): string {
    const rating = this.ratingValue;
    
    if (rating === 0) return 'Select a rating';
    if (rating <= 1) return 'Poor';
    if (rating <= 2) return 'Fair';
    if (rating <= 3) return 'Good';
    if (rating <= 4) return 'Very Good';
    return 'Excellent';
  }
  
  // ==========================================================================
  // NAVIGATION
  // ==========================================================================
  
  goBack(): void {
    this.router.navigate(['/client/my-bookings']);
  }
  
  // ==========================================================================
  // UTILITY METHODS
  // ==========================================================================
  
  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch (error) {
      console.error('Error formatting date:', error);
      return dateString;
    }
  }
  
  getDefaultImage(): string {
    return 'assets/images/download.jpg';
  }
  
  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = this.getDefaultImage();
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
      this.errorMessage = 'Booking not found.';
      setTimeout(() => this.router.navigate(['/client/my-bookings']), 2000);
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
