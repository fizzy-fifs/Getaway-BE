airbnb_schema = {
    "properties": {
        "location": {"type": "string"},
        "accommodation_location": {"type": "string"},
        "number_of_guest": {"type": "number"},
        "accommodation_dates": {"type": "string"},
        "accommodation_number_of_beds": {"type": "number"},
        "accommodation_number_of_rooms": {"type": "number"},
        "accommodation_number_of_bathrooms": {"type": "number"},
        "link_to_rooms": {"type": "string"},
        "accommodation_images_src": {"type": "array",
                                     "items": {
                                         "type": "string"
                                     }},
        "accommodation_total_price_for_selected_dates": {"type": "number"},
    },
    "required": ["title", "number_of_guest", "accommodation_dates",
                 "link_to_rooms", "accommodation_images_src",
                 "accommodation_total_price_for_selected_dates"]
}
