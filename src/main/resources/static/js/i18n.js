/**
 * Modern Internationalization support using Intl API
 */
class I18n {
  constructor() {
    this.messages = {};
    this.currentLocale = 'en';
    this.missingKeys = new Set();
    this.developmentMode = this.isDevelopmentMode();
    this.availableLocales = ['en', 'ca']; // Add more locales as they become available
  }

  async init() {
    // Try to load from localStorage first
    const storedLocale = localStorage.getItem('preferred-language');
    
    // Detect preferred browser language if no stored preference
    const browserLang = storedLocale || navigator.language.split('-')[0];
    await this.setLocale(browserLang);

    // Set up language selector if it exists
    const langSelector = document.getElementById('language');
    if (langSelector) {
      // Update selector to match current locale
      if (langSelector.querySelector(`option[value="${this.currentLocale}"]`)) {
        langSelector.value = this.currentLocale;
      }

      // Add event listener for language changes
      langSelector.addEventListener('change', e => {
        this.setLocale(e.target.value);
      });
    }
  }

  async setLocale(locale) {
    try {
      // Check if the requested locale is available, fallback to English if not
      if (!this.availableLocales.includes(locale)) {
        console.warn(`Locale ${locale} not available, falling back to English`);
        locale = 'en';
      }
      
      // Load messages directly from JSON file
      const response = await fetch(`/i18n/messages_${locale}.json`);
      
      if (!response.ok) {
        // Fallback to English if requested locale file not found
        if (locale !== 'en') {
          console.warn(`Locale ${locale} file not found, falling back to English`);
          return this.setLocale('en');
        }
        throw new Error(`Failed to load translations for ${locale}`);
      }
      
      this.messages = await response.json();
      this.currentLocale = locale;
      
      // Format dates and numbers according to the locale
      this.dateFormatter = new Intl.DateTimeFormat(locale, { 
        hour: '2-digit', 
        minute: '2-digit',
        second: '2-digit' 
      });
      
      // Update all translated elements in the DOM
      this.updateDOM();
      
      // Store preferred language
      localStorage.setItem('preferred-language', locale);
      
      console.log(`Loaded ${Object.keys(this.messages).length} translations for locale: ${locale}`);
      return true;
    } catch (error) {
      console.error('Error setting locale:', error);
      return false;
    }
  }

  /**
   * Get translated message with optional parameter substitution
   */
  t(key, ...args) {
    // Get message or use key as fallback
    const message = this.messages[key];
    
    // Check if key exists
    if (message === undefined) {
      // Keep track of missing keys
      if (this.developmentMode) {
        this.missingKeys.add(key);
        console.warn(`Missing translation key: ${key}`);
        return `⚠️ ${key} ⚠️`;
      } else {
        // In production, use key as fallback
        return key;
      }
    }
    
    // Replace placeholders like {0}, {1} with arguments
    return message.replace(/\{(\d+)\}/g, (match, index) => {
      return typeof args[index] !== 'undefined' ? args[index] : match;
    });
  }
  
  /**
   * Handle plural forms based on count
   * @param {string} key The base key without plural suffix
   * @param {number} count The count that determines which plural form to use
   * @param {Object} options Optional parameters 
   * @returns {string} The translated string with the appropriate plural form
   */
  plural(key, count, options = {}) {
    // Determine which plural form to use based on count
    let form;
    if (count === 0 && this.messages[`${key}.zero`]) {
      form = 'zero';
    } else if (count === 1) {
      form = 'one';
    } else {
      form = 'other';
    }
    
    // Try to get the specific plural form
    const pluralKey = `${key}.${form}`;
    const message = this.messages[pluralKey];
    
    if (message === undefined) {
      // Fallback to the base key if plural form not found
      if (this.messages[key]) {
        return this.t(key, count);
      }
      
      // Log missing key
      if (this.developmentMode) {
        this.missingKeys.add(pluralKey);
        console.warn(`Missing plural translation key: ${pluralKey}`);
        return `⚠️ ${pluralKey} ⚠️`;
      } else {
        return key;
      }
    }
    
    // Replace count placeholder and other parameters
    let result = message.replace(/\{0\}/g, options.formatNumber ? this.formatNumber(count) : count);
    
    // Replace additional parameters if provided
    if (options.params) {
      Object.entries(options.params).forEach(([key, value]) => {
        result = result.replace(new RegExp(`\{${key}\}`, 'g'), value);
      });
    }
    
    return result;
  }
  
  /**
   * Format a date using the current locale
   */
  formatDate(date) {
    if (!date) return '';
    
    // Convert string to Date object if needed
    if (typeof date === 'string') {
      date = new Date(date);
    }
    
    return this.dateFormatter.format(date);
  }
  
  /**
   * Format a number using the current locale
   */
  formatNumber(number) {
    return new Intl.NumberFormat(this.currentLocale).format(number);
  }
  
  /**
   * Update all elements with data-i18n attributes
   */
  updateDOM() {
    // Find all elements with data-i18n attribute
    const elements = document.querySelectorAll('[data-i18n]');
    
    elements.forEach(el => {
      const key = el.getAttribute('data-i18n');
      el.textContent = this.t(key);
    });
    
    // Find all elements with data-i18n-placeholder attribute
    const placeholders = document.querySelectorAll('[data-i18n-placeholder]');
    
    placeholders.forEach(el => {
      const key = el.getAttribute('data-i18n-placeholder');
      el.placeholder = this.t(key);
    });
    
    // Dispatch event for components that need to update
    document.dispatchEvent(new CustomEvent('i18n:updated'));
  }
  
  /**
   * Check if we're in development mode
   */
  isDevelopmentMode() {
    return window.location.hostname === 'localhost' || 
           window.location.hostname === '127.0.0.1';
  }
}

// Create singleton instance and expose to window
const i18n = new I18n();
window.i18n = i18n;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  i18n.init();
});

/**
 * Create a panel showing missing translation keys
 * Only shown in development mode
 */
function createMissingKeysPanel(missingKeys) {
  if (!missingKeys || missingKeys.size === 0) return;
  
  // Create panel if it doesn't exist
  let panel = document.getElementById('missing-i18n-keys');
  
  if (!panel) {
    panel = document.createElement('div');
    panel.id = 'missing-i18n-keys';
    panel.style.position = 'fixed';
    panel.style.bottom = '10px';
    panel.style.right = '10px';
    panel.style.backgroundColor = 'rgba(255, 200, 200, 0.9)';
    panel.style.padding = '10px';
    panel.style.borderRadius = '5px';
    panel.style.maxHeight = '300px';
    panel.style.overflowY = 'auto';
    panel.style.zIndex = '9999';
    panel.style.fontSize = '12px';
    panel.style.fontFamily = 'monospace';
    panel.style.boxShadow = '0 0 10px rgba(0,0,0,0.2)';
    
    // Add close button
    const closeBtn = document.createElement('button');
    closeBtn.textContent = 'X';
    closeBtn.style.float = 'right';
    closeBtn.style.background = 'none';
    closeBtn.style.border = 'none';
    closeBtn.style.cursor = 'pointer';
    closeBtn.style.fontSize = '16px';
    closeBtn.onclick = () => panel.remove();
    
    panel.appendChild(closeBtn);
    
    // Add title
    const title = document.createElement('h3');
    title.textContent = 'Missing Translation Keys';
    title.style.margin = '0 0 10px 0';
    panel.appendChild(title);
    
    document.body.appendChild(panel);
  }
  
  // Clear existing list
  const existingList = panel.querySelector('ul');
  if (existingList) {
    existingList.remove();
  }
  
  // Create list of missing keys
  const list = document.createElement('ul');
  list.style.margin = '0';
  list.style.padding = '0 0 0 20px';
  
  Array.from(missingKeys).sort().forEach(key => {
    const item = document.createElement('li');
    item.textContent = key;
    list.appendChild(item);
  });
  
  panel.appendChild(list);
}

// Helper functions for direct access
function t(key, ...args) {
  return window.i18n ? window.i18n.t(key, ...args) : key;
}

function plural(key, count, options) {
  return window.i18n ? window.i18n.plural(key, count, options) : key;
}

function formatDate(date) {
  return window.i18n ? window.i18n.formatDate(date) : date;
}

function changeLanguage(locale) {
  return window.i18n ? window.i18n.setLocale(locale) : false;
}

// Debug helper - available in console in development mode
if (window.i18n && window.i18n.developmentMode) {
  window.showAllTranslationKeys = function() {
    console.group('All translation keys:');
    Object.keys(window.i18n.messages).sort().forEach(key => {
      console.log(`${key} = ${window.i18n.messages[key]}`);
    });
    console.groupEnd();
    return `${Object.keys(window.i18n.messages).length} keys available`;
  };
}
