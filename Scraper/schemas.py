
airbnb_schema = {
    "properties": {
        "listing_title": {"type": "string"},
        "listing_location": {"type": "string"},
        "number_of_guest": {"type": "int"},
        "listing_dates": {"type": "date"},
        "listing_number_of_beds": {"type": "int"},
        "listing_number_of_rooms": {"type": "int"},
        "listing_number_of_bathrooms": {"type": "int"},
        "listing_price_per_night": {"type": "decimal"},
        "listing_total_price_for_selected_dates": {"type": "decimal"},

        "required": ["listing_location"]
    }
}
