<template>
  <canvas ref="canvas" class="starfield-canvas"></canvas>
</template>

<script>
export default {
  name: 'StarfieldBackground',
  data() {
    return {
      particles: [],
      mouse: { x: null, y: null },
      animId: null
    }
  },
  mounted() {
    this.init()
    window.addEventListener('resize', this.init)
    document.addEventListener('mousemove', this.onMouseMove)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.init)
    document.removeEventListener('mousemove', this.onMouseMove)
    cancelAnimationFrame(this.animId)
  },
  methods: {
    onMouseMove(e) {
      this.mouse.x = e.clientX
      this.mouse.y = e.clientY
    },
    init() {
      const canvas = this.$refs.canvas
      const dpr = window.devicePixelRatio || 1
      canvas.width = window.innerWidth * dpr
      canvas.height = window.innerHeight * dpr
      canvas.style.width = window.innerWidth + 'px'
      canvas.style.height = window.innerHeight + 'px'
      const ctx = canvas.getContext('2d')
      ctx.scale(dpr, dpr)

      const count = Math.floor((window.innerWidth * window.innerHeight) / 12000)
      this.particles = []
      for (let i = 0; i < count; i++) {
        this.particles.push({
          x: Math.random() * window.innerWidth,
          y: Math.random() * window.innerHeight,
          r: Math.random() * 1.5 + 0.3,
          vx: (Math.random() - 0.5) * 0.4,
          vy: (Math.random() - 0.5) * 0.4,
          alpha: Math.random() * 0.5 + 0.3
        })
      }
      this.draw(ctx)
    },
    draw(ctx) {
      const w = window.innerWidth
      const h = window.innerHeight
      ctx.clearRect(0, 0, w, h)

      // Connection lines between nearby particles
      for (let i = 0; i < this.particles.length; i++) {
        const p = this.particles[i]
        p.x += p.vx
        p.y += p.vy
        if (p.x < 0) p.x = w
        if (p.x > w) p.x = 0
        if (p.y < 0) p.y = h
        if (p.y > h) p.y = 0

        // Draw particle (violet/golden)
        ctx.beginPath()
        ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
        ctx.fillStyle = `rgba(167,139,250,${p.alpha})`
        ctx.fill()

        // Draw connections
        for (let j = i + 1; j < this.particles.length; j++) {
          const p2 = this.particles[j]
          const dx = p.x - p2.x
          const dy = p.y - p2.y
          const dist = Math.sqrt(dx * dx + dy * dy)
          if (dist < 120) {
            ctx.beginPath()
            ctx.moveTo(p.x, p.y)
            ctx.lineTo(p2.x, p2.y)
            ctx.strokeStyle = `rgba(167,139,250,${0.22 * (1 - dist / 120)})`
            ctx.lineWidth = 0.8
            ctx.stroke()
          }
        }

        // Mouse interaction: particles drift toward cursor
        if (this.mouse.x && this.mouse.y) {
          const mx = p.x - this.mouse.x
          const my = p.y - this.mouse.y
          const md = Math.sqrt(mx * mx + my * my)
          if (md < 200 && md > 1) {
            p.x -= mx / md * 0.5
            p.y -= my / md * 0.5
          }
        }
      }
      this.animId = requestAnimationFrame(() => this.draw(ctx))
    }
  }
}
</script>

<style scoped>
.starfield-canvas {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}
</style>
