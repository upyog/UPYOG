# EST + Dynamic Form — Simple Guide

This explains the shared “Dynamic*” pieces and how the **EST** module uses them.
Read top to bottom. Think of it like a **form wizard**: fill → review → submit.

---

## One-line meanings

| Piece | Simple meaning |
|--------|----------------|
| **DynamicFormField** | Draws **one** input (text, date, dropdown, file…). |
| **DynamicForm** | Draws the **whole form** by looping fields from config. |
| **DynamicFormStep** | One **wizard page**: header + merge MDMS config + DynamicForm. |
| **DynamicCheckPage** | **Review** screen before API submit (read-only summary). |
| **DynamicObjectRenderer** | Pretty-prints any **JSON object** as labels/values (helper UI). |

---

## Big idea (why “dynamic”?)

Fields are **not hardcoded in React**.

1. **MDMS** (`Estate.Config` / `assignAssetConfig`) describes fields (name, type, validation, dropdown source).
2. **Local overrides** (e.g. `estateFormConfig.js`) add EST-only rules (payload, computed fields).
3. Components **read that config** and render the UI.

Same engine for **registration**, **assign assets**, and **search**.

---

## Flow chart — how pieces connect

```mermaid
flowchart TB
  subgraph CONFIG["Config (not React UI)"]
    MDMS["MDMS Estate.Config<br/>field list"]
    LOCAL["Local overrides<br/>estateFormConfig.js etc."]
    MERGE["mergeRouteConfig()<br/>MDMS + local"]
    MDMS --> MERGE
    LOCAL --> MERGE
  end

  subgraph FILL["Fill step"]
    STEP["DynamicFormStep<br/>page wrapper + header"]
    FORM["DynamicForm<br/>form state + submit"]
    FIELD["DynamicFormField<br/>× each field"]
    STEP --> FORM --> FIELD
  end

  subgraph REVIEW["Review step"]
    CHECK["DynamicCheckPage<br/>summary + declare + Submit"]
  end

  subgraph HELPER["Optional helper"]
    OBJ["DynamicObjectRenderer<br/>show any object as UI"]
  end

  MERGE --> STEP
  FORM -->|"Save & Next<br/>saves session"| CHECK
  CHECK -->|"Submit"| API["Estate API"]
```

---

## EST employee flows (what you click in the UI)

### A) Create asset (registration)

```mermaid
flowchart LR
  A["/create-asset/newRegistration<br/>DynamicFormStep"] --> 
  B["/check<br/>ESTDynamicCheckPage<br/>→ DynamicCheckPage"]
  B --> C["/acknowledgement"]
```

- **NewRegistration** → `DynamicFormStep` + `estateFormConfig`
- User fills building, city, locality, rates…
- **Save & Next** → session stores values + `routeConfig`
- **Check** → `ESTDynamicCheckPage` wraps `DynamicCheckPage` → shows summary → API create

### B) Assign assets (allotment)

```mermaid
flowchart LR
  S["/search-applications<br/>DynamicForm search mode"] --> I["/assignassets/info"]
  I --> AA["/assignassets/assign-assets<br/>DynamicFormStep"]
  AA --> CK["/check<br/>DynamicCheckPage"]
  CK --> ACK["/acknowledgement"]
```

- Search uses **DynamicForm** with `mode="search"` (no wizard ActionBar).
- Assign form uses **DynamicFormStep** + `estateAllotmentFormOverrides`.

---

## Each piece in plain English

### 1. DynamicFormField

**Job:** Render **one** field.

- Reads: `fieldConfig` (label, type, validation) + `formData` + `dropdownData`
- Types: text, dropdown, date, file, radio, group, section header
- On change → calls `onChange(name, value)` up to DynamicForm

**Analogy:** One brick.

---

### 2. DynamicForm

**Job:** Own the **form state** and draw all fields.

- Loads dropdown options (`useDynamicMDMS`: MDMS masters, localities, city)
- Maps `routeConfig.form` → many `DynamicFormField`s
- Validates → builds payload → **Save & Next** / **Search** / draft

**Modes:**

| Mode | Used in EST for |
|------|------------------|
| `wizard` (default) | Registration / assign-assets |
| `search` | Search applications page |

**Analogy:** The whole wall made of bricks.

---

### 3. DynamicFormStep

**Job:** One **wizard route page**.

1. `mergeRouteConfig(MDMS step, localOverrides)`
2. Show **Header**
3. Render **DynamicForm**
4. On next → `onSelect(stepKey, data)` and attach `routeConfig` into session

EST wrappers are thin:

- `ESTNEWRegistration.js` → registration step  
- `ESTAssignAssets.js` → allotment step  

**Analogy:** One room in the house (form room).

---

### 4. DynamicCheckPage

**Job:** **Review** before submit (not editable form).

- Uses the **same** `routeConfig.form` to know which labels to show
- Reads saved wizard session values
- Edit icon jumps back to the form step
- Checkbox declaration → **Submit** → parent runs API

EST wrapper: `ESTDynamicCheckPage.js` (picks registration vs allotment payload).

**Analogy:** Checkout / “please confirm” page.

---

### 5. DynamicObjectRenderer

**Job:** Dump any object/array as readable label–value cards.

- **Not** part of the main EST wizard path
- Useful for debugging or showing nested API JSON
- No config, no validation, no submit

**Analogy:** A generic “print this object nicely” widget.

---

## Data flow (registration) — simplest path

```text
MDMS Config
    +
estateFormConfig.js
    ↓ merge
routeConfig.form
    ↓
DynamicFormStep
    ↓
DynamicForm  →  DynamicFormField (each row)
    ↓  Save & Next
Session storage (wizard params)
    ↓
DynamicCheckPage (summary)
    ↓  Submit
Estate create API
    ↓
Acknowledgement
```

---

## Where files live

| What | Where |
|------|--------|
| Shared Dynamic* UI | `packages/react-components/src/molecules/` |
| Merge / check helpers | `packages/react-components/src/utilities/checkPageUtils.js`, `formUtils.js`, `useDynamicMDMS.js` |
| EST registration step | `modules/est/src/PageComponents/ESTNEWRegistration.js` |
| EST assign step | `modules/est/src/PageComponents/ESTAssignAssets.js` |
| EST check wrapper | `modules/est/src/pages/employee/Create/ESTDynamicCheckPage.js` |
| EST local form rules | `modules/est/src/config/estateFormConfig.js`, `Create/estateAllotmentFormOverrides.js` |
| EST wizards | `modules/est/src/pages/employee/Create/index.js`, `AssignAssetIndex.js` |

---

## Cheat sheet — “When do I touch what?”

| I want to… | Change… |
|------------|---------|
| Add/rename a field | MDMS `Estate.Config` (and local override if needed) |
| Change payload / computed locality | `estateFormConfig.js` / allotment overrides |
| Change how one control looks | `DynamicFormField.js` (shared — careful) |
| Change Save & Next / draft / search bar | `DynamicForm.js` |
| Change review layout | `DynamicCheckPage.js` or EST check wrapper |
| Change wizard steps / routes | EST Create pages + MDMS body routes |

---

## Memory tip

```text
Config  →  Step (page)  →  Form (state)  →  Field (one input)
                              ↓
                         Check (review)
                              ↓
                            API
```

**DynamicObjectRenderer** sits outside this main path — only for displaying objects.
