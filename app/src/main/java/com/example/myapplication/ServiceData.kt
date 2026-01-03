package com.example.myapplication

data class ServiceCategory(
    val id: Int,
    val name: String,
    val icon: String,
    val services: List<Service>
)

data class Service(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val description: String,
    val estimatedPrice: String,
    val estimatedTime: String
)

class ServiceData {
    companion object {
        fun getAllServices(): List<ServiceCategory> {
            return listOf(
                ServiceCategory(
                    1,
                    "Smartphones & Tablets",
                    "📱",
                    listOf(
                        Service(1, 1, "Screen Replacement", "Cracked or damaged screen repair", "Rs. 12,000-22,500", "1-2 hours"),
                        Service(2, 1, "Battery Replacement", "Poor battery life or won't charge", "Rs. 6,000-12,000", "30 mins"),
                        Service(3, 1, "Water Damage Repair", "Phone got wet or submerged", "Rs. 15,000-30,000", "2-3 days"),
                        Service(4, 1, "Charging Port Fix", "Phone won't charge or loose connection", "Rs. 7,500-13,500", "1 hour"),
                        Service(5, 1, "Camera Repair", "Camera not working or blurry images", "Rs. 9,000-18,000", "1-2 hours")
                    )
                ),
                ServiceCategory(
                    2,
                    "Laptops & Computers",
                    "💻",
                    listOf(
                        Service(6, 2, "Laptop Not Charging", "Power adapter or charging port issues", "Rs. 9,000-18,000", "2-3 hours"),
                        Service(7, 2, "Screen Replacement", "Cracked or blank laptop screen", "Rs. 22,500-45,000", "1-2 days"),
                        Service(8, 2, "Keyboard Repair", "Keys not working or stuck", "Rs. 12,000-22,500", "2-4 hours"),
                        Service(9, 2, "Hard Drive Recovery", "Data recovery from damaged drive", "Rs. 15,000-37,500", "3-5 days"),
                        Service(10, 2, "Virus Removal", "Computer running slow or infected", "Rs. 7,500-15,000", "2-4 hours")
                    )
                ),
                ServiceCategory(
                    3,
                    "Televisions",
                    "📺",
                    listOf(
                        Service(11, 3, "Screen Repair", "Cracked TV screen or display issues", "Rs. 30,000-75,000", "3-5 days"),
                        Service(12, 3, "No Display", "TV turns on but no picture", "Rs. 15,000-37,500", "1-2 days"),
                        Service(13, 3, "Sound Problems", "No audio or distorted sound", "Rs. 12,000-27,000", "2-3 hours"),
                        Service(14, 3, "Remote Control Issues", "TV not responding to remote", "Rs. 4,500-9,000", "1 hour"),
                        Service(15, 3, "HDMI Port Repair", "HDMI ports not working", "Rs. 10,500-19,500", "2-4 hours")
                    )
                ),
                ServiceCategory(
                    4,
                    "Air Conditioners",
                    "❄️",
                    listOf(
                        Service(16, 4, "Not Cooling", "AC running but not cooling properly", "Rs. 12,000-22,500", "2-3 hours"),
                        Service(17, 4, "Strange Noises", "Unusual sounds from AC unit", "Rs. 9,000-18,000", "1-2 hours"),
                        Service(18, 4, "Refrigerant Leak", "AC leaking water or coolant", "Rs. 15,000-30,000", "3-4 hours"),
                        Service(19, 4, "Filter Replacement", "Dirty or clogged air filters", "Rs. 6,000-12,000", "30 mins"),
                        Service(20, 4, "Thermostat Issues", "Temperature control not working", "Rs. 10,500-21,000", "1-2 hours")
                    )
                ),
                ServiceCategory(
                    5,
                    "Refrigerators",
                    "🧊",
                    listOf(
                        Service(21, 5, "Not Cooling", "Fridge not maintaining temperature", "Rs. 15,000-30,000", "2-4 hours"),
                        Service(22, 5, "Ice Maker Problems", "Ice maker not working or leaking", "Rs. 12,000-24,000", "2-3 hours"),
                        Service(23, 5, "Strange Noises", "Unusual sounds from refrigerator", "Rs. 9,000-18,000", "1-2 hours"),
                        Service(24, 5, "Door Seal Issues", "Poor door seal causing energy loss", "Rs. 7,500-15,000", "1 hour"),
                        Service(25, 5, "Water Dispenser Fix", "Water dispenser not working", "Rs. 10,500-21,000", "1-2 hours")
                    )
                ),
                ServiceCategory(
                    6,
                    "Washing Machines",
                    "👔",
                    listOf(
                        Service(26, 6, "Won't Start", "Machine not turning on or starting", "Rs. 12,000-22,500", "2-3 hours"),
                        Service(27, 6, "Water Not Draining", "Water remains in drum after cycle", "Rs. 9,000-18,000", "1-2 hours"),
                        Service(28, 6, "Excessive Vibration", "Machine shaking or moving during cycle", "Rs. 7,500-15,000", "1 hour"),
                        Service(29, 6, "Not Spinning", "Drum not spinning properly", "Rs. 10,500-21,000", "2-3 hours"),
                        Service(30, 6, "Leaking Water", "Water leaking from machine", "Rs. 9,000-19,500", "1-2 hours")
                    )
                )
            )
        }
    }
}
