# User Interface Module

## Overview
This module provides a modern Java Swing graphical user interface for the Product Recommendation System.

## Purpose
- Allow users to search for products
- Filter by category and price range
- Display ranked recommendations
- Provide links to original Cdiscount product pages

## Components

### UI Classes

#### `MainFrame.java`
Main application window.

**Features:**
- 1200x800 windowed interface
- Split pane layout (search left, results right)
- Menu bar with File and Help menus
- Asynchronous data loading on startup
- Background search execution

**Entry Point:**
```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    });
}
```

#### `SearchPanel.java`
Left panel with search form.

**Controls:**
- Search query text field
- Category dropdown (All, smartphones, claviers, etc.)
- Min/Max price range fields
- Search button

**Getters:**
- `getSearchQuery()`: Get entered search text
- `getSelectedCategory()`: Get selected category
- `getMinPrice()`: Get minimum price filter
- `getMaxPrice()`: Get maximum price filter

#### `ResultsPanel.java`
Right panel displaying recommendations.

**Features:**
- Scrollable list of product cards
- Status header (result count, loading, errors)
- Auto-scroll to top on new results

**Methods:**
- `displayResults(results)`: Show recommendation list
- `showLoading()`: Display loading state
- `showError(message)`: Show error message

#### `ProductCard.java`
Individual product display component.

**Layout:**
```
┌────────────────────────────────────┐
│ 🥇 BEST RECOMMENDED                │
├────────────────────────────────────┤
│ Samsung Galaxy A15 4G              │
│ ★★★★★ (4.2) - 127 reviews         │
│ 98,99 €                            │
│ "Excellent écran..."               │
│ [🔗 View on Cdiscount]             │
└────────────────────────────────────┘
```

**Rank Badges:**
- 🥇 BEST (rank 1) - Gold color
- 🥈 (rank 2) - Silver color
- 🥉 (rank 3) - Bronze color
- #4, #5, ... - Default color

**Actions:**
- "View on Cdiscount" button opens original product URL in browser

#### `UIStyles.java`
Centralized styling and theme.

**Color Palette (Dark Theme):**
- Background Primary: `#1A202C` (dark blue-gray)
- Background Card: `#2D3748` (card background)
- Accent Primary: `#38A169` (green)
- Text Primary: `#FFFFFF` (white)
- Success: `#48BB78` (green)
- Warning: `#ED8936` (orange)

**Fonts:**
- Title: Segoe UI Bold 24pt
- Heading: Segoe UI Bold 18pt
- Body: Segoe UI Regular 13pt

**Helper Methods:**
- `createAccentButton(text)`: Green accent button with hover effect
- `createSecondaryButton(text)`: Secondary button with border
- `createTextField(placeholder)`: Styled text input
- `createComboBox(items)`: Styled dropdown
- `createCard()`: Card panel with padding and border

## How to Run

### Compile All Modules
```bash
# Compile preprocessing
cd c:\Studies\S7\JAVA\scrapmaven\03_preprocessing
javac -d bin src/com/recommendation/preprocessing/*.java

# Compile model
cd ..\04_recommendation_model
javac -d bin -cp ../03_preprocessing/bin src/com/recommendation/model/*.java

# Compile UI
cd ..\05_user_interface
javac -d bin -cp ../03_preprocessing/bin;../04_recommendation_model/bin src/com/recommendation/ui/*.java
```

### Run Application
```bash
cd c:\Studies\S7\JAVA\scrapmaven\05_user_interface
java -cp bin;../03_preprocessing/bin;../04_recommendation_model/bin com.recommendation.ui.MainFrame
```

## Usage Flow

### 1. Startup
- Application launches with loading screen
- CSV files are loaded from `02_data_collection/raw/`
- Products are preprocessed and engine is initialized
- Success message shows total product count

### 2. Search
- Enter search query (e.g., "samsung smartphone")
- Select category (optional)
- Set price range (optional)
- Click "Search Products"

### 3. View Results
- Recommendations appear ranked by score
- Best recommendation highlighted with 🥇
- Each card shows:
  - Product title
  - Rating and review count
  - Price
  - Description preview
  - Link to Cdiscount

### 4. Open Product
- Click "View on Cdiscount" button
- Original product page opens in default browser

## Menu Options

**File Menu:**
- Reload Data: Reload CSV files
- Exit: Close application

**Help Menu:**
- About: Show application information

## Error Handling

The UI handles several error scenarios:
- **Data loading failure**: Shows error dialog and message
- **Invalid price input**: Defaults to 0/MAX_VALUE
- **No results**: Displays "No products found" message
- **Link opening failure**: Shows error dialog

## Next Steps

For full project documentation, see:
- `docs/PROJECT_README.md` - Complete project overview
- `docs/project_log.md` - File/folder creation log
