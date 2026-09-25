# UPYOG SV-UI Upgrade Guide

Upgrade from **Node 14 / React 17 / React-Router-Dom v5 / Axios 0.21.x / React Query v3 / babel-preset-react 6.x / i18next 19.x / react-i18next 11.x / date-fns v2 / react-tooltip v4**
to **Node 22 / React 19 / React-Router-Dom v6 / Axios 1.x / TanStack Query v5 / @babel/preset-react 7.x / i18next 23.x / react-i18next 14.x / date-fns v3 / react-tooltip v5**.

---

## Dependency Versions

| Package | Before | After |
|---|---|---|
| Node | 14.x | 22.x |
| Yarn | 1.22.19 | 1.22.22 |
| React | 17.x | 19.0.0 |
| react-dom | 17.x | 19.0.0 |
| react-router-dom | v5 | v6 |
| react-query | v3 | @tanstack/react-query v5 |
| Build Tool | CRA | Vite |
| Axios | 0.21.x | 1.x |
| react-i18next | 11.x | 14.x |
| i18next | 19.x | 23.x |
| Babel Preset React | babel-preset-react 6.x | @babel/preset-react 7.x |
| react-tooltip | v4 | v5 |
| date-fns | 2.30.0 | 3.6.0 |

---

## Clean Install

Remove all `node_modules`, `dist`, and `yarn.lock` files from the following paths before reinstalling:

```
web/package.json
web/yarn.lock
web/micro-ui-internals/node_modules
web/micro-ui-internals/yarn.lock
web/micro-ui-internals/example/node_modules
web/micro-ui-internals/packages/css/dist
web/micro-ui-internals/packages/css/node_modules
web/micro-ui-internals/packages/libraries/dist
web/micro-ui-internals/packages/libraries/node_modules
web/micro-ui-internals/packages/modules/common/dist
web/micro-ui-internals/packages/modules/common/node_modules
web/micro-ui-internals/packages/modules/core/dist
web/micro-ui-internals/packages/modules/core/node_modules
web/micro-ui-internals/packages/modules/engagement/dist
web/micro-ui-internals/packages/modules/engagement/node_modules
web/micro-ui-internals/packages/modules/sv/dist
web/micro-ui-internals/packages/modules/sv/node_modules
web/micro-ui-internals/packages/react-components/dist
web/micro-ui-internals/packages/react-components/node_modules
```

Then run:

```bash
yarn cache clean
yarn install          # inside web/
yarn install          # inside web/micro-ui-internals/
```

---

## Issues Fixed

### 1. Node 14 → 22

- Upgrade Node.js to v22.
- Upgrade Yarn to the latest version.
- Upgrade `react-scripts` from `3.6.1` to `5.0.1` (can be removed once Vite is fully set up).
- Replace `"node": ">=14"` with `"node": ">=22"` in all `package.json` files.

---

### 2. React 17 → 19

#### `key` prop must not be passed via spread

In React 19, passing `key` inside a spread object is an error.

```jsx
// Before — invalid in React 19
const props = { key: "abc", name: "John" };
<Component {...props} />

// After — correct way
const { key, ...rest } = props;
<Component key={key} {...rest} />
```

**Affected file:** `web/micro-ui-internals/packages/react-components/src/molecules/FormStep.js`

**Error:**
```
A props object containing a "key" prop is being spread into JSX.
React keys must be passed directly to JSX without using spread.
```

---

#### `useEffect` must not return `null` or a Promise

In React 19, a `useEffect` callback must return either a cleanup function or nothing (`undefined`). Returning `null` or a Promise throws an error.

```js
// Before — invalid, returns null or a Promise
useEffect(() => (condition ? mutate({ tenantId }) : null), [tenantId]);

// After — correct way
useEffect(() => {
  if (condition) {
    mutate({ tenantId });
  }
}, [tenantId]);
```

**Affected file:** `web/micro-ui-internals/packages/modules/engagement/src/pages/citizen/NotificationsAndWhatsNew.js`

**Error:**
```
TypeError: destroy is not a function
```

---

#### `key` prop missing in list renders

The `key` prop was optional (warning only) in React 17 but is required for correct list rendering in React 19. Ensure every list-rendered element has a unique `key`.

---

#### `ReactDOM.render()` removed — use `createRoot()` instead

---

#### `defaultProps` deprecated — use ES6 default parameters instead

---

### 3. React Router DOM v5 → v6

- `<Switch>` → `<Routes>`
- `useHistory()` → `useNavigate()`
- `<Redirect>` → `<Navigate>`
- `component={}` → `element={<Comp />}`
- `history.push()` → `navigate()`
- `history.push('/path')` → `navigate()`

**Error:**
```
Error: [AppContainer] is not a <Route> component. All component children of
<Routes> must be a <Route> or <React.Fragment>.
```

`<Routes>` only accepts `<Route>` components as direct children — layout components must be placed outside.

```jsx
// Before
<span className="sv-citizen">
  <AppContainer>
    <BackButton>Back</BackButton>
    <Routes>
      ...
    </Routes>
  </AppContainer>
</span>

// After Right way
<AppContainer>
  <span className="sv-citizen">
    <BackButton>Back</BackButton>
    <Routes>
      ...
    </Routes>
  </span>
</AppContainer>
```

---

### 4. React Query → TanStack Query v5

-  `react-query` -> `@tanstack/react-query` 
// ❌ OLD — react-query
import { useQuery, useQueryClient } from "react-query";

// ✅ NEW — @tanstack/react-query
import { useQuery, useQueryClient } from "@tanstack/react-query";
Replace react-query with @tanstack/react-query everywhere

s.no | Error | Before | After |
|---|---|---|---|
| 1. | react-query import | from "react-query" | from "@tanstack/react-query" |
| 2. | useQuery syntax | useQuery('key', fn) | useQuery({queryKey, queryFn}) |
| 3. | useMutation syntax | useMutation(fn) | useMutation({mutationFn}) |


