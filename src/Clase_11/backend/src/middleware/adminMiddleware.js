/**
 * Admin authorization middleware.
 * Assumes authMiddleware has already executed and req.user is populated.
 */
const adminMiddleware = (req, res, next) => {
    if (req.user?.isAdmin === true) {
        return next();
    }

    return res.status(403).json({error: 'Forbidden: Access denied'});
};

module.exports = adminMiddleware;