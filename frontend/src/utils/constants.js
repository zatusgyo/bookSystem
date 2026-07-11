/**
 * 默认图书封面 — 内联 SVG Base64
 * 当图书无封面图时使用，避免 example.com 占位图加载失败
 */
export const DEFAULT_COVER = 'data:image/svg+xml;base64,' + btoa(`
<svg xmlns="http://www.w3.org/2000/svg" width="200" height="280" viewBox="0 0 200 280">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#667eea"/>
      <stop offset="100%" style="stop-color:#764ba2"/>
    </linearGradient>
  </defs>
  <rect width="200" height="280" rx="8" fill="url(#bg)"/>
  <rect x="10" y="10" width="180" height="260" rx="4" fill="none" stroke="rgba(255,255,255,0.3)" stroke-width="1"/>
  <text x="100" y="100" text-anchor="middle" fill="white" font-size="36" font-family="Arial">📖</text>
  <text x="100" y="145" text-anchor="middle" fill="rgba(255,255,255,0.9)" font-size="14" font-family="Arial,sans-serif">图书封面</text>
  <text x="100" y="170" text-anchor="middle" fill="rgba(255,255,255,0.6)" font-size="10" font-family="Arial,sans-serif">BookSystem</text>
</svg>
`)
