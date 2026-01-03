package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SupportActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnCallSupport: Button
    private lateinit var btnWhatsApp: Button
    private lateinit var btnEmailSupport: Button
    private lateinit var llFAQs: LinearLayout
    private lateinit var btnPhoneTips: Button
    private lateinit var btnLaptopTips: Button
    private lateinit var btnTVTips: Button
    private lateinit var btnApplianceTips: Button
    private lateinit var llMaintenanceTips: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        // Initialize views
        initViews()

        // Load FAQs
        loadFAQs()

        // Load default maintenance tips (phone tips)
        loadMaintenanceTips("phone")

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCallSupport = findViewById(R.id.btnCallSupport)
        btnWhatsApp = findViewById(R.id.btnWhatsApp)
        btnEmailSupport = findViewById(R.id.btnEmailSupport)
        llFAQs = findViewById(R.id.llFAQs)
        btnPhoneTips = findViewById(R.id.btnPhoneTips)
        btnLaptopTips = findViewById(R.id.btnLaptopTips)
        btnTVTips = findViewById(R.id.btnTVTips)
        btnApplianceTips = findViewById(R.id.btnApplianceTips)
        llMaintenanceTips = findViewById(R.id.llMaintenanceTips)
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCallSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+94117778888")
            startActivity(intent)
        }

        btnWhatsApp.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://api.whatsapp.com/send?phone=94771234567&text=Hello TechCare Support, I need help with...")
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmailSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:support@techcare.lk")
            intent.putExtra(Intent.EXTRA_SUBJECT, "TechCare Support Request")
            intent.putExtra(Intent.EXTRA_TEXT, "Hello TechCare Team,\n\nI need assistance with:\n\n")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }

        // Maintenance tip category buttons
        btnPhoneTips.setOnClickListener {
            loadMaintenanceTips("phone")
            updateTipButtons("phone")
        }

        btnLaptopTips.setOnClickListener {
            loadMaintenanceTips("laptop")
            updateTipButtons("laptop")
        }

        btnTVTips.setOnClickListener {
            loadMaintenanceTips("tv")
            updateTipButtons("tv")
        }

        btnApplianceTips.setOnClickListener {
            loadMaintenanceTips("appliance")
            updateTipButtons("appliance")
        }
    }

    private fun loadFAQs() {
        val faqs = listOf(
            Pair("How long does a typical repair take?", "Most repairs are completed within 1-3 days. Complex issues like motherboard repairs may take up to 5-7 days. We'll provide an estimated timeline when you drop off your device."),
            Pair("Do you provide warranty on repairs?", "Yes! We offer 6 months warranty on all repairs. If the same issue occurs within the warranty period, we'll fix it free of charge."),
            Pair("What payment methods do you accept?", "We accept cash, credit/debit cards, bank transfers, and mobile payments (Dialog eZ Cash, Mobitel mCash, Hutch PayLo)."),
            Pair("Can I track my repair status?", "Yes! You can track your repair status in the app under 'My Bookings' or call our hotline with your request number."),
            Pair("Do you offer home pickup service?", "Yes, we provide home pickup service in Colombo, Kandy, and Galle areas. Pickup charges apply based on location."),
            Pair("What if my device cannot be repaired?", "If your device is beyond economical repair, we'll inform you and return it without charge. We can also help you with data recovery if needed."),
            Pair("How much does a typical repair cost?", "Repair costs vary by device and issue. Basic repairs start from Rs. 2,000. You'll receive a free diagnosis and quote before any work begins."),
            Pair("Do I need an appointment?", "While walk-ins are welcome, we recommend booking through the app to avoid waiting times and ensure faster service."),
            Pair("What should I bring when dropping off my device?", "Bring your device, charger (if applicable), and a valid ID. Remove any personal accessories like cases or screen protectors."),
            Pair("Can you repair water-damaged devices?", "Yes, we specialize in water damage repairs. Success depends on how quickly you bring the device and the extent of damage. Turn off the device immediately if it gets wet!")
        )

        faqs.forEach { (question, answer) ->
            addFAQItem(question, answer)
        }
    }

    private fun addFAQItem(question: String, answer: String) {
        val faqView = LayoutInflater.from(this).inflate(android.R.layout.simple_expandable_list_item_1, llFAQs, false) as TextView

        faqView.text = "Q: $question"
        faqView.textSize = 14f
        faqView.setTextColor(resources.getColor(android.R.color.black, null))
        faqView.setPadding(16, 12, 16, 12)
        faqView.setBackgroundColor(resources.getColor(android.R.color.background_light, null))

        var isExpanded = false

        faqView.setOnClickListener {
            if (isExpanded) {
                faqView.text = "Q: $question"
                isExpanded = false
            } else {
                faqView.text = "Q: $question\n\nA: $answer"
                isExpanded = true
            }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 8)
        faqView.layoutParams = params

        llFAQs.addView(faqView)
    }

    private fun updateTipButtons(selectedCategory: String) {
        // Reset all button colors
        btnPhoneTips.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)
        btnLaptopTips.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)
        btnTVTips.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)
        btnApplianceTips.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)

        // Highlight selected button
        when (selectedCategory) {
            "phone" -> btnPhoneTips.backgroundTintList = resources.getColorStateList(android.R.color.holo_blue_bright, null)
            "laptop" -> btnLaptopTips.backgroundTintList = resources.getColorStateList(android.R.color.holo_orange_dark, null)
            "tv" -> btnTVTips.backgroundTintList = resources.getColorStateList(android.R.color.holo_green_dark, null)
            "appliance" -> btnApplianceTips.backgroundTintList = resources.getColorStateList(android.R.color.holo_purple, null)
        }
    }

    private fun loadMaintenanceTips(category: String) {
        llMaintenanceTips.removeAllViews()

        val tips = when (category) {
            "phone" -> listOf(
                "🔋 Battery Care: Avoid letting your battery drain completely. Charge between 20-80% for optimal battery life.",
                "🌡️ Temperature: Keep your phone away from extreme heat or cold. Don't leave it in direct sunlight or cars.",
                "🧽 Cleaning: Use a microfiber cloth to clean your screen. Avoid harsh chemicals or abrasive materials.",
                "📱 Screen Protection: Use a quality screen protector and protective case to prevent damage from drops.",
                "💧 Water Protection: Keep your phone away from water. If it gets wet, turn it off immediately and seek help.",
                "🔄 Updates: Keep your phone software updated for security and performance improvements.",
                "📁 Storage: Regularly clean up photos, videos, and apps you don't use to free up storage space.",
                "🔌 Charging: Use original or certified chargers. Avoid overcharging by unplugging when fully charged."
            )
            "laptop" -> listOf(
                "🌬️ Ventilation: Keep air vents clear of dust and debris. Use compressed air to clean them monthly.",
                "🔋 Battery: Don't keep your laptop plugged in 24/7. Let the battery discharge and recharge occasionally.",
                "💾 Storage: Run disk cleanup regularly and keep at least 15% of your hard drive free.",
                "🖥️ Screen: Close your laptop gently and clean the screen with appropriate cleaners only.",
                "⌨️ Keyboard: Clean keyboard regularly with compressed air. Be gentle with the keys.",
                "🔄 Updates: Install Windows updates and antivirus software to keep your system secure.",
                "🌡️ Temperature: Use your laptop on hard, flat surfaces. Avoid beds or soft surfaces that block airflow.",
                "💿 Backup: Regularly backup important files to external storage or cloud services."
            )
            "tv" -> listOf(
                "🧽 Cleaning: Turn off and unplug before cleaning. Use a dry microfiber cloth for the screen.",
                "🌡️ Ventilation: Ensure proper ventilation around your TV. Don't block air vents.",
                "⚡ Power: Use a surge protector to protect against power fluctuations.",
                "📺 Brightness: Avoid maximum brightness settings to extend screen life.",
                "🔌 Connections: Check and clean cable connections regularly for better signal quality.",
                "🔄 Updates: Update your TV's firmware when available for better performance.",
                "📱 Remote: Keep your remote away from liquids and replace batteries when low.",
                "🏠 Placement: Keep your TV away from direct sunlight and heat sources."
            )
            "appliance" -> listOf(
                "❄️ AC Maintenance: Clean or replace filters monthly. Schedule professional servicing every 6 months.",
                "🧊 Refrigerator: Clean coils at the back every 6 months. Keep vents clear for proper airflow.",
                "👔 Washing Machine: Clean the lint filter after every use. Run an empty hot cycle monthly with vinegar.",
                "⚡ Power: Use voltage regulators for sensitive appliances to protect against power fluctuations.",
                "🔧 Professional Service: Schedule annual maintenance for major appliances like AC and refrigerators.",
                "💧 Water Quality: Use water filters if you have hard water to prevent mineral buildup.",
                "🧽 Cleaning: Keep appliances clean and dry. Wipe down surfaces regularly.",
                "📖 Manual: Read and follow manufacturer's maintenance guidelines for best results."
            )
            else -> emptyList()
        }

        tips.forEach { tip ->
            addTipItem(tip)
        }
    }

    private fun addTipItem(tip: String) {
        val tipView = TextView(this)
        tipView.text = tip
        tipView.textSize = 14f
        tipView.setTextColor(resources.getColor(android.R.color.black, null))
        tipView.setPadding(16, 12, 16, 12)
        tipView.setBackgroundColor(resources.getColor(android.R.color.background_light, null))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 8)
        tipView.layoutParams = params

        llMaintenanceTips.addView(tipView)
    }
}
