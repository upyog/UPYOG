// Router compatibility layer for React Router v5 to v6 migration
import React from 'react';
import {
  Routes,
  Route as RouteV6,
  Navigate,
  useNavigate,
  useLocation as useLocationV6,
  useParams as useParamsV6,
  useMatch,
} from 'react-router-dom';

// Export everything else directly from v6
export {
  BrowserRouter,
  HashRouter,
  Link,
  NavLink,
  Outlet,
  useNavigate,
  useParams,
  useSearchParams,
} from 'react-router-dom';

// Wrapper for Switch -> Routes
export const Switch = ({ children }) => {
  return <Routes>{children}</Routes>;
};

// Wrapper for Route component (v5 style to v6)
export const Route = ({ component: Component, render, children, path, exact, ...rest }) => {
  // Handle component prop
  if (Component) {
    return <RouteV6 path={path} element={<Component {...rest} />} />;
  }
  
  // Handle render prop
  if (render) {
    return <RouteV6 path={path} element={render(rest)} />;
  }
  
  // Handle children
  return <RouteV6 path={path} element={children} />;
};

// useHistory -> useNavigate wrapper
export const useHistory = () => {
  const navigate = useNavigate();
  const location = useLocationV6();
  
  return {
    push: (path, state) => navigate(path, { state }),
    replace: (path, state) => navigate(path, { replace: true, state }),
    go: (n) => navigate(n),
    goBack: () => navigate(-1),
    goForward: () => navigate(1),
    location,
    listen: () => {
      console.warn('history.listen is not supported in this compatibility layer');
      return () => {};
    },
  };
};

// useRouteMatch wrapper
export const useRouteMatch = (pathOrOptions) => {
  const location = useLocationV6();
  const params = useParamsV6();
  
  // If called without arguments, return current match info
  if (!pathOrOptions) {
    const path = location.pathname;
    return {
      path,
      url: path,
      isExact: true,
      params,
    };
  }
  
  // If string path provided
  if (typeof pathOrOptions === 'string') {
    const match = useMatch(pathOrOptions);
    if (match) {
      return {
        path: pathOrOptions,
        url: match.pathname,
        isExact: match.pathname === location.pathname,
        params: match.params,
      };
    }
    return null;
  }
  
  // If options object provided
  const { path, exact, strict } = pathOrOptions;
  const match = useMatch({ path, end: exact, caseSensitive: strict });
  
  if (match) {
    return {
      path,
      url: match.pathname,
      isExact: match.pathname === location.pathname,
      params: match.params,
    };
  }
  
  return null;
};

// useLocation wrapper
export const useLocation = () => {
  return useLocationV6();
};

// Redirect component wrapper
export const Redirect = ({ to, from, push, ...rest }) => {
  return <Navigate to={to} replace={!push} {...rest} />;
};

// withRouter HOC wrapper
export const withRouter = (Component) => {
  return (props) => {
    const navigate = useNavigate();
    const location = useLocationV6();
    const params = useParamsV6();
    
    const history = {
      push: (path, state) => navigate(path, { state }),
      replace: (path, state) => navigate(path, { replace: true, state }),
      go: (n) => navigate(n),
      goBack: () => navigate(-1),
      goForward: () => navigate(1),
      location,
    };
    
    return <Component {...props} history={history} location={location} match={{ params }} />;
  };
};

// Prompt component (removed in v6, provide a stub or custom implementation)
export const Prompt = ({ when, message }) => {
  React.useEffect(() => {
    if (when) {
      const handleBeforeUnload = (e) => {
        e.preventDefault();
        e.returnValue = message;
        return message;
      };
      
      window.addEventListener('beforeunload', handleBeforeUnload);
      return () => window.removeEventListener('beforeunload', handleBeforeUnload);
    }
  }, [when, message]);
  
  return null;
};

// useQueryParams custom hook (using URLSearchParams)
export const useQueryParams = () => {
  const location = useLocationV6();
  return React.useMemo(() => {
    const searchParams = new URLSearchParams(location.search);
    const params = {};
    for (let [key, value] of searchParams.entries()) {
      params[key] = value;
    }
    return params;
  }, [location.search]);
};