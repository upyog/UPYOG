const inboxSearchFields = {
    
};

export const getSearchFields = (isInbox) => (isInbox ? inboxSearchFields : null);
