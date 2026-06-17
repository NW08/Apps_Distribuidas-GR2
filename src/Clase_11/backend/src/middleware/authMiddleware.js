const jwt = require('jsonwebtoken');

/**
 * JWT Authentication middleware.
 * Extracts the token from the Authorization header (Bearer <token>),
 * verifies it, and attaches the payload to req.user if valid.
 */
const authMiddleware = (req, res, next) => {
    const authHeader = req.get('authorization');

    if (!authHeader?.startsWith('Bearer ')) {
        return res.status(401).json({error: 'Unauthorized: Missing or invalid token format'});
    }

    const token = authHeader.split(' ')[1];

    try {
        req.user = jwt.verify(token, process.env.JWT_SECRET);

        return next();
    } catch (error) {
        return res.status(401).json({error: 'Unauthorized: Invalid or expired token'});
    }
};

module.exports = authMiddleware;