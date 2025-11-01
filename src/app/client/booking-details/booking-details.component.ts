import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';

// pdf generator wrapper (html2canvas + jsPDF)
import html2pdf from 'html2pdf.js';

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
  selector: 'app-booking-details',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatDividerModule
  ],
  templateUrl: './booking-details.component.html',
  styleUrls: ['./booking-details.component.css']
})
export class BookingDetailsComponent implements OnInit {

  /** The WHOLE printable area must be wrapped with #pdfContent in the template */
  @ViewChild('pdfContent', { static: false }) pdfContent!: ElementRef<HTMLElement>;

  // API base
  private readonly API_BASE_URL = 'http://localhost:8080/v1/api';

  // Data
  booking: BookingResponse | null = null;
  propertyDetails: PropertyDetails | null = null;
  bookingId: number | null = null;

  // UI state
  isLoading = false;
  isPropertyLoading = false;
  isGenerating = false;

  // Messages
  errorMessage = '';
  successMessage = '';

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  // ==========================================================================
  // LIFECYCLE
  // ==========================================================================
  ngOnInit(): void {
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
            this.loadPropertyDetails(booking.propertyId);
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

  loadPropertyDetails(propertyId: number): void {
    this.isPropertyLoading = true;

    const url = `${this.API_BASE_URL}/client/viewClickedProperty/${propertyId}`;

    this.http.get<ApiResponse<PropertyDetails>>(url).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.propertyDetails = response.data;
        } else {
          console.warn('Failed to load property details');
        }
        this.isPropertyLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Failed to load property details:', error);
        this.isPropertyLoading = false;
      }
    });
  }

  // ==========================================================================
  // NAVIGATION
  // ==========================================================================
  goBack(): void {
    this.router.navigate(['/client/my-bookings']);
  }

  viewPropertyDetails(): void {
    if (this.booking) {
      this.router.navigate(['/client/property-details', this.booking.propertyId]);
    }
  }

  // ==========================================================================
  // PDF GENERATION - FULL PAGE with background CSS
  // ==========================================================================
  async downloadPDF() {
    const root = document.querySelector('.booking-downloader') as HTMLElement | null;
    if (!root) { console.error('PDF root not found'); return; }

    // Add a temporary "pdf-mode" class so we can restyle just for export
    root.classList.add('pdf-mode');

    // Optional: lock width to A4 @ 96dpi (≈ 794px) so layout doesn’t collapse
    // We’ll also read the actual computed gradient to carry into the clone.
    const bg = getComputedStyle(root).background || 'linear-gradient(135deg, #fff, #fff)';

    // Wait for web fonts (less blurriness)
    if ((document as any).fonts?.ready) {
      try { await (document as any).fonts.ready; } catch { /* ignore */ }
    }

    // html2pdf options
    const options: any = {
      margin: [10, 10, 10, 10],                        // mm
      filename: `booking-${this.bookingId}.pdf`,
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: {
        scale: 2,                                      // sharp text
        useCORS: true,
        letterRendering: true,
        backgroundColor: '#ffffff',
        windowWidth: 1200,                             // enough layout width before we fix to A4
        scrollX: 0, scrollY: 0,
        onclone: (doc: Document) => {
          // 1) Re-select the export node in the cloned document
          const cloned = doc.querySelector('.booking-downloader') as HTMLElement | null;
          if (!cloned) return;

          // 2) Force an A4 friendly canvas width so html2canvas doesn’t squeeze things
          // 794px ≈ A4 width at 96dpi. We keep padding inside with CSS.
          cloned.style.width = '794px';
          cloned.style.maxWidth = '794px';
          cloned.style.margin = '0 auto';

          // 3) Apply the same gradient background to the cloned element
          cloned.style.background = bg;

          // 4) Inject “print skin” overrides *inside the clone* so the snapshot looks clean
          const style = doc.createElement('style');
          style.textContent = `
            :root {
              --gb-bg0: #ffffff;
              --gb-bg1: #ffffff;
              --pdf-padding: 16px;
              --pdf-border: #e6e8ec;
              --pdf-muted: #6b7280;
              --pdf-text: #111827;
            }

            /* base */
            .booking-downloader.pdf-mode,
            .booking-downloader {
              box-sizing: border-box;
              padding: var(--pdf-padding);
              color: var(--pdf-text);
              background: ${bg};
            }
            .booking-downloader * { box-sizing: inherit; }

            /* Cards look better flat in PDF */
            .mat-mdc-card, .mat-card, .mat-elevation-z* {
              box-shadow: none !important;
              border: 1px solid var(--pdf-border) !important;
              background: #fff !important;
            }

            /* Images: avoid rounded overflow cutting */
            img { max-width: 100%; height: auto; border-radius: 6px; }

            /* Typography stronger for print */
            h1,h2,h3 { color: var(--pdf-text); margin: 0 0 8px; }
            p,span,div { color: var(--pdf-text); }

            /* Chips / badges */
            .mat-mdc-chip, .mat-chip {
              background: #f3f4f6 !important;
              color: #111827 !important;
              border-radius: 16px !important;
            }

            /* Material form outlines often render faint in canvas;
               use solid borders for clarity */
            .mat-mdc-form-field .mdc-notched-outline__leading,
            .mat-mdc-form-field .mdc-notched-outline__notch,
            .mat-mdc-form-field .mdc-notched-outline__trailing {
              border-color: #d1d5db !important;
            }

            /* Page breaks: avoid cutting cards/sections */
            .avoid-break { break-inside: avoid; page-break-inside: avoid; }
            .page-break { break-before: page; page-break-before: always; }

            /* Remove sticky/positioned UI that can overlap in snapshot */
            .sticky, [style*="position: sticky"] { position: static !important; }
          `;
          doc.head.appendChild(style);

          // 5) Make external images CORS-friendly (needs correct server headers)
          doc.querySelectorAll('img').forEach((img: any) => {
            if (img.src && !img.src.startsWith('data:')) {
              img.setAttribute('crossorigin', 'anonymous');
            }
          });
        }
      },
      jsPDF: { unit: 'mm', format: 'a4', orientation: 'p' },
      pagebreak: {
        mode: ['css', 'legacy'],
        avoid: ['.avoid-break']
      }
    };

    try {
      await (html2pdf() as any).set(options).from(root).save();
    } finally {
      // Clean up
      root.classList.remove('pdf-mode');
    }
  }

  // ==========================================================================
  // UTILS
  // ==========================================================================
  mapBookingStatus(backendStatus: string): string {
    const statusMapping: { [key: string]: string } = {
      'CONFIRMED': 'BOOKED',
      'PENDING': 'BOOKED',
      'CANCELLED': 'CLOSED',
      'COMPLETED': 'CLOSED',
      'BOOKED': 'BOOKED',
      'CLOSED': 'CLOSED'
    };
    return statusMapping[backendStatus] || backendStatus;
  }

  getBookingStatusClass(status: string): string {
    const mapped = this.mapBookingStatus(status);
    const statusClasses: { [key: string]: string } = {
      'BOOKED': 'status-booked',
      'CLOSED': 'status-closed'
    };
    return statusClasses[mapped] || 'status-default';
  }

  getBookingStatusIcon(status: string): string {
    const mapped = this.mapBookingStatus(status);
    const statusIcons: { [key: string]: string } = {
      'BOOKED': 'check_circle',
      'CLOSED': 'lock'
    };
    return statusIcons[mapped] || 'help';
  }

  getBookingStatusText(status: string): string {
    return this.mapBookingStatus(status);
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
        month: 'long',
        day: 'numeric'
      });
    } catch {
      return dateString;
    }
  }

  formatDateTime(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return dateString;
    }
  }

  calculateNights(checkinDate: string, checkoutDate: string): number {
    const checkin = new Date(checkinDate);
    const checkout = new Date(checkoutDate);
    const diffTime = checkout.getTime() - checkin.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  calculateTotalPrice(pricePerDay: number, nights: number): number {
    return pricePerDay * nights;
  }

  getDefaultImage(): string {
    return 'assets/images/download.jpg';
  }

  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = this.getDefaultImage();
  }

  getRatingStars(rating: number): string {
    const full = Math.floor(rating);
    const half = rating % 1 >= 0.5 ? 1 : 0;
    const empty = 5 - full - half;
    return '★'.repeat(full) + (half ? '½' : '') + '☆'.repeat(empty);
  }

  formatPrice(price: number): string {
    return `₹${price.toLocaleString()}`;
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
