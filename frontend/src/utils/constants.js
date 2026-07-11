/**
 * 默认图书封面 — 内联 SVG（URL编码，兼容中文/Emoji）
 * 当图书无封面图时使用
 */
const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="200" height="280" viewBox="0 0 200 280">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#667eea"/>
      <stop offset="100%" style="stop-color:#764ba2"/>
    </linearGradient>
  </defs>
  <rect width="200" height="280" rx="8" fill="url(#bg)"/>
  <rect x="10" y="10" width="180" height="260" rx="4" fill="none" stroke="rgba(255,255,255,0.3)" stroke-width="2"/>
  <text x="100" y="110" text-anchor="middle" fill="white" font-size="48" font-weight="bold">B</text>
  <text x="100" y="155" text-anchor="middle" fill="rgba(255,255,255,0.9)" font-size="14" font-family="sans-serif">BookSystem</text>
</svg>`
export const DEFAULT_COVER = 'data:image/svg+xml,' + encodeURIComponent(svg)

