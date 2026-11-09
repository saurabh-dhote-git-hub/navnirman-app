import React from 'react'
import { createRoot } from 'react-dom/client'
import './style.css'

const categories = ['History', 'Technology', 'Science', 'Culture', 'Geography']

function App() {
  return (
    <main className="shell">
      <nav className="nav">
        <div className="brand">NAVNIRMAN <span>नवनिर्माण</span></div>
        <div className="nav-links"><a href="#explore">Explore</a><a href="#plans">Premium</a><button>Sign in</button></div>
      </nav>
      <section className="hero">
        <p className="eyebrow">KNOWLEDGE • STORIES • IDEAS</p>
        <h1>Discover more.<br/><em>Understand deeper.</em></h1>
        <p className="intro">A home for thoughtful videos on history, technology, science and the world around us.</p>
        <button className="primary">Explore videos <span>→</span></button>
      </section>
      <section id="explore" className="section">
        <div className="section-head"><div><p className="eyebrow">CURATED FOR THE CURIOUS</p><h2>Explore categories</h2></div><span className="muted">More perspectives. More understanding.</span></div>
        <div className="categories">{categories.map((c, i) => <article className={`category c${i}`} key={c}><span>0{i+1}</span><h3>{c}</h3><p>Discover stories and ideas</p></article>)}</div>
      </section>
      <footer><span>NAVNIRMAN · नवनिर्माण</span><span>Learn without limits.</span></footer>
    </main>
  )
}

createRoot(document.getElementById('root')).render(<App />)
