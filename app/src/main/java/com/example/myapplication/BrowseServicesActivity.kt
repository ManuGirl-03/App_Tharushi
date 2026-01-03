package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BrowseServicesActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var etSearch: EditText
    private lateinit var llServiceCategories: LinearLayout
    private lateinit var serviceCategories: List<ServiceCategory>
    private lateinit var filteredServices: MutableList<Service>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_services)

        // Initialize views
        initViews()

        // Load service data
        loadServiceData()

        // Set click listeners
        setClickListeners()

        // Display all services initially
        displayServices()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        etSearch = findViewById(R.id.etSearch)
        llServiceCategories = findViewById(R.id.llServiceCategories)
    }

    private fun loadServiceData() {
        serviceCategories = ServiceData.getAllServices()
        filteredServices = mutableListOf()
        serviceCategories.forEach { category ->
            filteredServices.addAll(category.services)
        }
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterServices(s.toString())
            }
        })
    }

    private fun filterServices(query: String) {
        filteredServices.clear()

        if (query.isEmpty()) {
            serviceCategories.forEach { category ->
                filteredServices.addAll(category.services)
            }
        } else {
            serviceCategories.forEach { category ->
                category.services.forEach { service ->
                    if (service.name.contains(query, ignoreCase = true) ||
                        service.description.contains(query, ignoreCase = true) ||
                        category.name.contains(query, ignoreCase = true)) {
                        filteredServices.add(service)
                    }
                }
            }
        }

        displayServices()
    }

    private fun displayServices() {
        llServiceCategories.removeAllViews()

        val groupedServices = filteredServices.groupBy { service ->
            serviceCategories.find { it.id == service.categoryId }
        }

        groupedServices.forEach { (category, services) ->
            if (category != null && services.isNotEmpty()) {
                addCategoryHeader(category)
                services.forEach { service ->
                    addServiceItem(service)
                }
            }
        }
    }

    private fun addCategoryHeader(category: ServiceCategory) {
        val headerView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, llServiceCategories, false)
        val textView = headerView.findViewById<TextView>(android.R.id.text1)
        textView.text = "${category.icon} ${category.name}"
        textView.textSize = 18f
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
        textView.setTextColor(resources.getColor(android.R.color.black, null))
        textView.setPadding(16, 24, 16, 8)
        llServiceCategories.addView(headerView)
    }

    private fun addServiceItem(service: Service) {
        val serviceView = LayoutInflater.from(this).inflate(R.layout.item_service, llServiceCategories, false)

        val tvServiceName = serviceView.findViewById<TextView>(R.id.tvServiceName)
        val tvServiceDescription = serviceView.findViewById<TextView>(R.id.tvServiceDescription)
        val tvServicePrice = serviceView.findViewById<TextView>(R.id.tvServicePrice)
        val tvServiceTime = serviceView.findViewById<TextView>(R.id.tvServiceTime)

        tvServiceName.text = service.name
        tvServiceDescription.text = service.description
        tvServicePrice.text = service.estimatedPrice
        tvServiceTime.text = service.estimatedTime

        serviceView.setOnClickListener {
            // Navigate to service request activity
            val intent = Intent(this, ServiceRequestActivity::class.java)
            intent.putExtra("service_id", service.id)
            intent.putExtra("service_name", service.name)
            intent.putExtra("service_description", service.description)
            intent.putExtra("service_price", service.estimatedPrice)
            intent.putExtra("service_time", service.estimatedTime)
            startActivity(intent)
        }

        llServiceCategories.addView(serviceView)
    }
}
