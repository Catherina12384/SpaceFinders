package Com.Spacefinders.Service;

import Com.Spacefinders.DTO.Response.PropertyDetailResponse;
import Com.Spacefinders.DTO.Response.PropertyResponse;
import Com.Spacefinders.Entity.Address;
import Com.Spacefinders.Entity.Property;
import Com.Spacefinders.Entity.User;
import Com.Spacefinders.Exception.PropertyNotFoundException;
import Com.Spacefinders.Exception.UserNotFoundException;
import Com.Spacefinders.Repository.AddressRepository;
import Com.Spacefinders.Repository.PropertyRepository;
import Com.Spacefinders.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    // Get Property by ID
    public PropertyDetailResponse getPropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));

        Address address = addressRepository.findById(property.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        User host = userRepository.findById(property.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Host not found"));

        return convertToPropertyDetailResponse(property, address, host);
    }

    // Get All Properties
    public List<PropertyResponse> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();

        List<PropertyResponse> propertyResponses = new ArrayList<>();
        for (Property property : properties) {
            propertyResponses.add(convertToPropertyResponse(property));
        }

        return propertyResponses;
    }

    // Get Properties by User ID
    public List<PropertyResponse> getPropertiesByUserId(Long userId) {
        List<Property> properties = propertyRepository.findByUserId(userId);

        List<PropertyResponse> propertyResponses = new ArrayList<>();
        for (Property property : properties) {
            propertyResponses.add(convertToPropertyResponse(property));
        }

        return propertyResponses;
    }

    // Helper method to convert Property to PropertyResponse
    private PropertyResponse convertToPropertyResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
        response.setPropertyId(property.getPropertyId());
        response.setPropertyName(property.getPropertyName());
        response.setPropertyDescription(property.getPropertyDescription());
        response.setNoOfRooms(property.getNoOfRooms());
        response.setNoOfBathrooms(property.getNoOfBathrooms());
        response.setMaxNoOfGuests(property.getMaxNoOfGuests());
        response.setPricePerDay(property.getPricePerDay());
        response.setImageURL(property.getImageURL());
        response.setPropertyStatus(property.getPropertyStatus().toString());
        response.setPropertyRate(property.getPropertyRate());
        response.setPropertyRatingCount(property.getPropertyRatingCount());
        response.setHasWifi(property.getHasWifi());
        response.setHasParking(property.getHasParking());
        response.setHasPool(property.getHasPool());
        response.setHasAc(property.getHasAc());
        response.setHasHeater(property.getHasHeater());
        response.setHasPetFriendly(property.getHasPetFriendly());

        // Get address details
        Address address = addressRepository.findById(property.getAddressId()).orElse(null);
        if (address != null) {
            response.setCity(address.getCity());
            response.setState(address.getState());
            response.setCountry(address.getCountry());
        }

        return response;
    }

    // Helper method to convert to PropertyDetailResponse
    private PropertyDetailResponse convertToPropertyDetailResponse(Property property, Address address, User host) {
        PropertyDetailResponse response = new PropertyDetailResponse();
        response.setPropertyId(property.getPropertyId());
        response.setPropertyName(property.getPropertyName());
        response.setPropertyDescription(property.getPropertyDescription());
        response.setNoOfRooms(property.getNoOfRooms());
        response.setNoOfBathrooms(property.getNoOfBathrooms());
        response.setMaxNoOfGuests(property.getMaxNoOfGuests());
        response.setPricePerDay(property.getPricePerDay());
        response.setImageURL(property.getImageURL());
        response.setPropertyStatus(property.getPropertyStatus().toString());
        response.setPropertyRate(property.getPropertyRate());
        response.setPropertyRatingCount(property.getPropertyRatingCount());
        response.setHasWifi(property.getHasWifi());
        response.setHasParking(property.getHasParking());
        response.setHasPool(property.getHasPool());
        response.setHasAc(property.getHasAc());
        response.setHasHeater(property.getHasHeater());
        response.setHasPetFriendly(property.getHasPetFriendly());

        // Address details
        response.setBuildingNo(address.getBuildingNo());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPostalCode(address.getPostalCode());

        // Host details
        response.setHostId(host.getUserId());
        response.setHostName(host.getUsername());
        response.setHostPhone(host.getUserPhone());

        return response;
    }
}