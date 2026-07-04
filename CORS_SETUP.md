# Connecting the frontend to this backend (CORS)

This backend only accepts browser requests from an allowed origin, set via the `FRONTEND_URL`
environment variable (see `SecurityConfig.java` and `application.properties`).

## Local development
No action needed — it defaults to `http://localhost:5173` (Vite's default dev server port).

## After deploying the frontend
1. Deploy the frontend first (e.g. to Vercel) and copy its URL, e.g. `https://hirehub.vercel.app`.
2. On Render, go to your backend Web Service → **Environment** → add:
   ```
   FRONTEND_URL=https://hirehub.vercel.app
   ```
3. Redeploy the backend (Render does this automatically when you save an env var).

If you ever get a browser console error like `blocked by CORS policy`, it almost always means
this value doesn't match your frontend's actual deployed URL exactly (check for `https://` vs
`http://`, and no trailing slash).
