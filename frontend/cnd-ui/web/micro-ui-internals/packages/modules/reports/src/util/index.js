/**
 * index.js (Reports Utilities)
 *
 * Purpose:
 * Utility helpers specific to reports verification logic.
 *
 * Responsibilities:
 * - Defines the role check function (checkForEmployee) to authorize page links.
 */

export const checkForEmployee = (roles) => {
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const userInfo = Digit.UserService.getUser();
    let rolesArray = [];

    const rolearray = userInfo?.info?.roles.filter((item) => {
        for (let i = 0; i < roles.length; i++) {
            if (item.code == roles[i] && item.tenantId === tenantId) rolesArray.push(true);
        }
    });

    return rolesArray?.length;
};
