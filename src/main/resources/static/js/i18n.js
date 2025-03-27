/**
 * Modern Internationalization support using Intl API
 */
class I18n {
  constructor() {
    this.messages = {};
    this.currentLocale = 'en';
    this.missingKeys = new Set();
    this.developmentMode = this.isDevelopmentMode();
  }

  async init() {
    // Detect preferred browser language
    const browserLang = navigator.language.split('-')[0];
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
      // Try to load messages for requested locale
      const response = await fetch(`/api/messages?lang=${locale}`);
      
      if (!response.ok) {
        // Fallback to English if requested locale not available
        if (locale !== 'en') {
          console.warn(`Locale ${locale} not available, falling back to English`);
          return this.setLocale('en');
        }
        throw new Error('Failed to load translations');
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
    // Determine which plural form to use based on the language and count
    const pluralForm = this.getPluralForm(count);
    const pluralKey = `${key}.${pluralForm}`;
    
    // Check if specific plural form exists
    if (this.messages[pluralKey]) {
      // Include the count as the first argument by default
      const args = options.args || [];
      return this.t(pluralKey, count, ...args);
    }
    
    // If no specific plural form found, try the general key
    if (this.messages[key]) {
      const args = options.args || [];
      return this.t(key, count, ...args);
    }
    
    // No translation found
    if (this.developmentMode) {
      this.missingKeys.add(pluralKey);
      console.warn(`Missing plural translation key: ${pluralKey}`);
      return `⚠️ ${pluralKey} (${count}) ⚠️`;
    }
    
    return key;
  }
  
  /**
   * Get the plural form based on count and current locale
   * Different languages have different plural rules
   * @param {number} count The count value
   * @returns {string} The plural form name ('zero', 'one', 'two', 'few', 'many', 'other')
   */
  getPluralForm(count) {
    // Use Intl.PluralRules to determine the correct plural category
    try {
      const pluralRules = new Intl.PluralRules(this.currentLocale);
      return pluralRules.select(count);
    } catch (error) {
      // Fallback for browsers that don't support Intl.PluralRules
      return count === 1 ? 'one' : 'other';
    }
  }
  
  /**
   * Check if application is running in development mode
   */
  isDevelopmentMode() {
    // Simple check - if we're on localhost or a local IP
    return window.location.hostname === 'localhost' || 
           window.location.hostname === '127.0.0.1' ||
           window.location.hostname.startsWith('192.168.') ||
           window.location.hostname.startsWith('10.') ||
           window.location.port !== '';
  }

  /**
   * Format a date according to the current locale
   */
  formatDate(date) {
    return this.dateFormatter.format(date);
  }

  /**
   * Update all elements with translation attributes
   */
  updateDOM() {
    // Update text content
    document.querySelectorAll('[data-i18n]').forEach(el => {
      el.textContent = this.t(el.getAttribute('data-i18n'));
    });
    
    // Update placeholders
    document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
      el.placeholder = this.t(el.getAttribute('data-i18n-placeholder'));
    });
    
    // Update buttons
    document.querySelectorAll('[data-i18n-value]').forEach(el => {
      el.value = this.t(el.getAttribute('data-i18n-value'));
    });
    
    // Update titles/tooltips
    document.querySelectorAll('[data-i18n-title]').forEach(el => {
      el.title = this.t(el.getAttribute('data-i18n-title'));
    });
    
    // Update aria-labels
    document.querySelectorAll('[data-i18n-aria-label]').forEach(el => {
      el.setAttribute('aria-label', this.t(el.getAttribute('data-i18n-aria-label')));
    });
    
    // Emit event for dynamic components to update
    document.dispatchEvent(new CustomEvent('i18n:updated', {
      detail: { locale: this.currentLocale }
    }));
  }
}

// Create singleton instance and expose to window
const i18n = new I18n();
window.i18n = i18n;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  i18n.init().then(() => {
    console.log(`Initialized i18n with locale: ${i18n.currentLocale}`);
    
    // In development mode, add a panel showing missing translation keys if any
    if (i18n.developmentMode && i18n.missingKeys.size > 0) {
      createMissingKeysPanel(i18n.missingKeys);
    }
  });
});

/**
 * Create a panel showing missing translation keys
 */
function createMissingKeysPanel(missingKeys) {
  // Create panel container
  const panel = document.createElement('div');
  panel.style.position = 'fixed';
  panel.style.bottom = '10px';
  panel.style.right = '10px';
  panel.style.width = '300px';
  panel.style.maxHeight = '200px';
  panel.style.overflowY = 'auto';
  panel.style.background = '#ffebee';
  panel.style.color = '#c62828';
  panel.style.padding = '10px';
  panel.style.borderRadius = '4px';
  panel.style.boxShadow = '0 2px 5px rgba(0,0,0,0.2)';
  panel.style.zIndex = '9999';
  panel.style.fontSize = '12px';
  
  // Add header
  const header = document.createElement('div');
  header.style.fontWeight = 'bold';
  header.style.marginBottom = '5px';
  header.style.display = 'flex';
  header.style.justifyContent = 'space-between';
  header.textContent = `Missing Translation Keys (${missingKeys.size})`;
  
  // Add close button
  const closeBtn = document.createElement('span');
  closeBtn.textContent = '×';
  closeBtn.style.cursor = 'pointer';
  closeBtn.style.fontSize = '16px';
  closeBtn.style.lineHeight = '12px';
  closeBtn.onclick = () => panel.remove();
  header.appendChild(closeBtn);
  
  panel.appendChild(header);
  
  // Add missing keys list
  const list = document.createElement('ul');
  list.style.margin = '0';
  list.style.padding = '0 0 0 20px';
  
  Array.from(missingKeys).forEach(key => {
    const item = document.createElement('li');
    item.textContent = key;
    list.appendChild(item);
  });
  
  panel.appendChild(list);
  
  // Add panel to body
  document.body.appendChild(panel);
}

/**
 * Helper functions for direct access
 */
function t(key, ...args) {
  return window.i18n.t(key, ...args);
}

function plural(key, count, options) {
  return window.i18n.plural(key, count, options);
}

function formatDate(date) {
  return window.i18n.formatDate(date);
}

function changeLanguage(locale) {
  return window.i18n.setLocale(locale);
}

// Debug helper - available in console in development mode
if (window.i18n && window.i18n.developmentMode) {
  window.showAllTranslationKeys = function() {
    console.group('Available translation keys');
    Object.keys(window.i18n.messages).sort().forEach(key => {
      console.log(`${key}: "${window.i18n.messages[key]}"`);
    });
    console.groupEnd();
    return `Found ${Object.keys(window.i18n.messages).length} translation keys`;
  };
}
