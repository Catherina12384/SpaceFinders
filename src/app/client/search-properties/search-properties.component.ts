import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';

// ============================================================================
// INTERFACES
// ============================================================================

interface Property {
  propertyId: number;
  propertyName: string;
  propertyDescription: string;
  noOfRooms: number;
  noOfBathrooms: number;
  maxNoOfGuests: number;
  pricePerDay: number;
  imageURL: string;
  city: string;
  state: string;
  country: string;
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

interface SearchFilters {
  city: string;
  state: string;
  country: string;
  minPrice: number;
  maxPrice: number;
  minRooms: number;
  minBathrooms: number;
  minGuests: number;
  amenities: string[];
  status: string;
}

// ============================================================================
// COMPONENT
// ============================================================================

@Component({
  selector: 'app-search-properties',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule
  ],
  templateUrl: './search-properties.component.html',
  styleUrls: ['./search-properties.component.css']
})
export class SearchPropertiesComponent implements OnInit {
  
  // ==========================================================================
  // PROPERTIES
  // ==========================================================================
  
  private readonly API_BASE_URL = 'http://localhost:8080/v1/api';
  
  // Data
  allProperties: Property[] = [];
  filteredProperties: Property[] = [];
  
  // Unique values for filters
  cities: string[] = [];
  states: string[] = [];
  countries: string[] = [];
  
  // Search filters
  searchFilters: SearchFilters = {
    city: '',
    state: '',
    country: '',
    minPrice: 0,
    maxPrice: 100000,
    minRooms: 0,
    minBathrooms: 0,
    minGuests: 0,
    amenities: [],
    status: 'AVAILABLE'
  };
  
  // UI state
  isLoading = false;
  showFilters = false;
  searchTerm = '';
  sortBy = 'price';
  sortOrder: 'asc' | 'desc' = 'asc';
  
  // Messages
  errorMessage = '';
  successMessage = '';
  
  // Available amenities
  availableAmenities = [
    { key: 'hasWifi', label: 'WiFi', icon: 'wifi' },
    { key: 'hasParking', label: 'Parking', icon: 'local_parking' },
    { key: 'hasPool', label: 'Pool', icon: 'pool' },
    { key: 'hasAc', label: 'Air Conditioning', icon: 'ac_unit' },
    { key: 'hasHeater', label: 'Heater', icon: 'whatshot' },
    { key: 'hasPetFriendly', label: 'Pet Friendly', icon: 'pets' }
  ];
  
  // Status options
  statusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'AVAILABLE', label: 'Available' },
    { value: 'UNAVAILABLE', label: 'Unavailable' },
    { value: 'UNDER_MAINTENANCE', label: 'Under Maintenance' }
  ];
  
  // ==========================================================================
  // CONSTRUCTOR
  // ==========================================================================
  
  constructor(
    private http: HttpClient,
    private router: Router
  ) {}
  
  // ==========================================================================
  // LIFECYCLE HOOKS
  // ==========================================================================
  
  ngOnInit(): void {
    this.loadAllProperties();
  }
  
  // ==========================================================================
  // API CALLS
  // ==========================================================================
  
  loadAllProperties(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const url = `${this.API_BASE_URL}/admin/viewAllProperties`;
    
    this.http.get<ApiResponse<Property[]>>(url, { headers }).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.allProperties = response.data;
          this.extractFilterOptions();
          this.applyFilters();
          this.showSuccess(`Loaded ${this.allProperties.length} properties`);
        } else {
          this.showError('Failed to load properties');
        }
        this.isLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Failed to load properties', error);
        this.isLoading = false;
      }
    });
  }
  
  // ==========================================================================
  // FILTER METHODS
  // ==========================================================================
  
  private extractFilterOptions(): void {
    const citiesSet = new Set<string>();
    const statesSet = new Set<string>();
    const countriesSet = new Set<string>();
    
    this.allProperties.forEach(property => {
      if (property.city) citiesSet.add(property.city);
      if (property.state) statesSet.add(property.state);
      if (property.country) countriesSet.add(property.country);
    });
    
    this.cities = Array.from(citiesSet).sort();
    this.states = Array.from(statesSet).sort();
    this.countries = Array.from(countriesSet).sort();
  }
  
  applyFilters(): void {
    this.filteredProperties = this.allProperties.filter(property => {
      // Text search (searches in name, description, city, state, country)
      const searchLower = this.searchTerm.toLowerCase();
      const matchesSearch = !this.searchTerm || 
        property.propertyName.toLowerCase().includes(searchLower) ||
        property.propertyDescription.toLowerCase().includes(searchLower) ||
        property.city.toLowerCase().includes(searchLower) ||
        property.state.toLowerCase().includes(searchLower) ||
        property.country.toLowerCase().includes(searchLower);
      
      // Location filters
      const matchesCity = !this.searchFilters.city || 
        property.city === this.searchFilters.city;
      
      const matchesState = !this.searchFilters.state || 
        property.state === this.searchFilters.state;
      
      const matchesCountry = !this.searchFilters.country || 
        property.country === this.searchFilters.country;
      
      // Status filter
      const matchesStatus = !this.searchFilters.status || 
        property.propertyStatus === this.searchFilters.status;
      
      // Price range filter
      const matchesPrice = property.pricePerDay >= this.searchFilters.minPrice && 
        property.pricePerDay <= this.searchFilters.maxPrice;
      
      // Room count filter
      const matchesRooms = this.searchFilters.minRooms === 0 || 
        property.noOfRooms >= this.searchFilters.minRooms;
      
      // Bathroom count filter
      const matchesBathrooms = this.searchFilters.minBathrooms === 0 || 
        property.noOfBathrooms >= this.searchFilters.minBathrooms;
      
      // Guest capacity filter
      const matchesGuests = this.searchFilters.minGuests === 0 || 
        property.maxNoOfGuests >= this.searchFilters.minGuests;
      
      // Amenities filter (all selected amenities must be present)
      const matchesAmenities = this.searchFilters.amenities.length === 0 || 
        this.searchFilters.amenities.every(amenity => 
          property[amenity as keyof Property] === true
        );
      
      return matchesSearch && matchesCity && matchesState && matchesCountry && 
             matchesStatus && matchesPrice && matchesRooms && matchesBathrooms && 
             matchesGuests && matchesAmenities;
    });
    
    this.sortProperties();
    
    if (this.searchTerm || this.hasActiveFilters()) {
      this.showSuccess(`Found ${this.filteredProperties.length} properties`);
    }
  }
  
  hasActiveFilters(): boolean {
    return this.searchFilters.city !== '' ||
           this.searchFilters.state !== '' ||
           this.searchFilters.country !== '' ||
           this.searchFilters.status !== 'AVAILABLE' ||
           this.searchFilters.minPrice > 0 ||
           this.searchFilters.maxPrice < 100000 ||
           this.searchFilters.minRooms > 0 ||
           this.searchFilters.minBathrooms > 0 ||
           this.searchFilters.minGuests > 0 ||
           this.searchFilters.amenities.length > 0;
  }
  
  clearFilters(): void {
    this.searchFilters = {
      city: '',
      state: '',
      country: '',
      minPrice: 0,
      maxPrice: 100000,
      minRooms: 0,
      minBathrooms: 0,
      minGuests: 0,
      amenities: [],
      status: 'AVAILABLE'
    };
    this.searchTerm = '';
    this.applyFilters();
    this.showSuccess('Filters cleared');
  }
  
  toggleAmenity(amenity: string): void {
    const index = this.searchFilters.amenities.indexOf(amenity);
    if (index > -1) {
      this.searchFilters.amenities.splice(index, 1);
    } else {
      this.searchFilters.amenities.push(amenity);
    }
  }
  
  sortProperties(): void {
    this.filteredProperties.sort((a, b) => {
      let comparison = 0;
      
      switch (this.sortBy) {
        case 'price':
          comparison = a.pricePerDay - b.pricePerDay;
          break;
        case 'rating':
          comparison = a.propertyRate - b.propertyRate;
          break;
        case 'name':
          comparison = a.propertyName.localeCompare(b.propertyName);
          break;
        case 'rooms':
          comparison = a.noOfRooms - b.noOfRooms;
          break;
        case 'guests':
          comparison = a.maxNoOfGuests - b.maxNoOfGuests;
          break;
        default:
          comparison = a.pricePerDay - b.pricePerDay;
      }
      
      return this.sortOrder === 'asc' ? comparison : -comparison;
    });
  }
  
  // ==========================================================================
  // NAVIGATION
  // ==========================================================================
  
  viewPropertyDetails(propertyId: number): void {
    this.router.navigate(['/client/property-details', propertyId]);
  }
  
  goBack(): void {
    this.router.navigate(['/client/dashboard']);
  }
  
  // ==========================================================================
  // UI HELPERS
  // ==========================================================================
  
  getRatingStars(rating: number): string {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;
    
    return '★'.repeat(fullStars) + (halfStar ? '½' : '') + '☆'.repeat(emptyStars);
  }
  
  getDefaultImage(): string {
    return 'assets/images/download.jpg';
  }
  
  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = this.getDefaultImage();
  }
  
  formatPrice(price: number): string {
    return `₹${price.toLocaleString()}`;
  }
  
  getActiveFiltersCount(): number {
    let count = 0;
    if (this.searchFilters.city) count++;
    if (this.searchFilters.state) count++;
    if (this.searchFilters.country) count++;
    if (this.searchFilters.status && this.searchFilters.status !== 'AVAILABLE') count++;
    if (this.searchFilters.minPrice > 0) count++;
    if (this.searchFilters.maxPrice < 100000) count++;
    if (this.searchFilters.minRooms > 0) count++;
    if (this.searchFilters.minBathrooms > 0) count++;
    if (this.searchFilters.minGuests > 0) count++;
    count += this.searchFilters.amenities.length;
    return count;
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
      this.errorMessage = 'Properties endpoint not found.';
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
