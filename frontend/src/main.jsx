import React, { useEffect, useMemo, useState } from 'react'
import { createRoot } from 'react-dom/client'
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { api, clearToken, getToken, setToken } from './api'
import './styles.css'

const MEALS = [
  { key: 'BREAKFAST', label: 'Breakfast', emoji: '☀️' },
  { key: 'LUNCH', label: 'Lunch', emoji: '🥗' },
  { key: 'DINNER', label: 'Dinner', emoji: '🍽️' },
  { key: 'SNACK', label: 'Snack', emoji: '🍎' },
]

const fmt = (value, digits = 0) => Number(value || 0).toLocaleString(undefined, {
  maximumFractionDigits: digits,
})

const dateKey = (d = new Date()) => {
  const y = d.getFullYear()
  const m = `${d.getMonth() + 1}`.padStart(2, '0')
  const day = `${d.getDate()}`.padStart(2, '0')
  return `${y}-${m}-${day}`
}

const labelDate = (iso, withYear = false) => {
  const [y, m, d] = iso.split('-').map(Number)
  return new Date(y, m - 1, d).toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    ...(withYear ? { year: 'numeric' } : {}),
  })
}

function App() {
  const [user, setUser] = useState(null)
  const [screen, setScreen] = useState('dashboard')
  const [authMode, setAuthMode] = useState('login')
  const [ready, setReady] = useState(false)
  const [flash, setFlash] = useState('')

  useEffect(() => {
    if (!getToken()) {
      setReady(true)
      return
    }
    api.me()
      .then(setUser)
      .catch(() => clearToken())
      .finally(() => setReady(true))
  }, [])

  const onAuth = (payload) => {
    setToken(payload.token)
    setUser(payload.user)
    setScreen('dashboard')
  }

  const logout = () => {
    clearToken()
    setUser(null)
    setFlash('')
  }

  if (!ready) return <div className="boot">Loading CalorieTrack…</div>
  if (!user) {
    return <AuthScreen mode={authMode} setMode={setAuthMode} onAuth={onAuth} />
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">C</div>
          <div>
            <strong>CalorieTrack</strong>
            <span>Daily nutrition</span>
          </div>
        </div>

        <nav>
          <NavItem icon="⌂" label="Dashboard" active={screen === 'dashboard'} onClick={() => setScreen('dashboard')} />
          <NavItem icon="＋" label="Log food" active={screen === 'log'} onClick={() => setScreen('log')} />
          <NavItem icon="◷" label="History" active={screen === 'history'} onClick={() => setScreen('history')} />
          <NavItem icon="⚙" label="Settings" active={screen === 'settings'} onClick={() => setScreen('settings')} />
        </nav>

        <div className="sidebar-foot">
          <div className="avatar">{user.displayName.slice(0, 1).toUpperCase()}</div>
          <div className="profile-mini">
            <strong>{user.displayName}</strong>
            <span>{user.email}</span>
          </div>
          <button className="icon-button" title="Log out" onClick={logout}>↪</button>
        </div>
      </aside>

      <main className="main">
        {flash && <div className="toast">{flash}</div>}
        {screen === 'dashboard' && <Dashboard user={user} onNavigate={setScreen} />}
        {screen === 'log' && <LogFood onSaved={(msg) => { setFlash(msg); setTimeout(() => setFlash(''), 2500) }} />}
        {screen === 'history' && <History />}
        {screen === 'settings' && <Settings user={user} onSaved={(next) => { setUser(next); setFlash('Settings saved'); setTimeout(() => setFlash(''), 2500) }} />}
      </main>
    </div>
  )
}

function NavItem({ icon, label, active, onClick }) {
  return <button className={`nav-item ${active ? 'active' : ''}`} onClick={onClick}>
    <span>{icon}</span>{label}
  </button>
}

function AuthScreen({ mode, setMode, onAuth }) {
  const [form, setForm] = useState({ displayName: '', email: '', password: '' })
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  const submit = async (e) => {
    e.preventDefault()
    setBusy(true); setError('')
    try {
      const result = mode === 'login'
        ? await api.login({ email: form.email, password: form.password })
        : await api.register(form)
      onAuth(result)
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">C</div>
        <div className="eyebrow">PERSONAL NUTRITION</div>
        <h1>{mode === 'login' ? 'Welcome back' : 'Start tracking'}</h1>
        <p className="muted">{mode === 'login' ? 'Keep your daily nutrition on track.' : 'Create your account and build a clear picture of what you eat.'}</p>

        {error && <div className="error-box">{error}</div>}

        <form onSubmit={submit} className="form-stack">
          {mode === 'register' && (
            <label>Display name
              <input required minLength="2" value={form.displayName} onChange={e => setForm({ ...form, displayName: e.target.value })} placeholder="Your name" />
            </label>
          )}
          <label>Email
            <input required type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="you@example.com" />
          </label>
          <label>Password
            <input required minLength="8" type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="Minimum 8 characters" />
          </label>
          <button className="primary large" disabled={busy}>{busy ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}</button>
        </form>

        <button className="switch-auth" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError('') }}>
          {mode === 'login' ? 'New here? Create an account' : 'Already have an account? Sign in'}
        </button>

        <div className="auth-note">Food nutrition is sourced from USDA FoodData Central through the app backend.</div>
      </div>
    </div>
  )
}

function Dashboard({ user, onNavigate }) {
  const [dashboard, setDashboard] = useState(null)
  const [entries, setEntries] = useState([])
  const [loading, setLoading] = useState(true)

  const load = async () => {
    setLoading(true)
    try {
      const [d, e] = await Promise.all([api.dashboard(), api.getEntries(dateKey())])
      setDashboard(d)
      setEntries(e)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  if (loading && !dashboard) return <PageLoader />

  const today = dashboard?.today || {}
  const goal = dashboard?.dailyGoal || user.dailyGoal || 2000
  const eaten = Number(today.calories || 0)
  const remaining = Math.max(goal - eaten, 0)
  const progress = Math.min((eaten / goal) * 100, 100)

  const macroTotal = Number(today.protein || 0) + Number(today.carbs || 0) + Number(today.fat || 0)
  const macroPct = (n) => macroTotal ? Math.round((Number(n || 0) / macroTotal) * 100) : 0

  return (
    <>
      <Topbar title="Dashboard" subtitle={`Today · ${labelDate(dashboard.date, true)}`} />
      <section className="hero-grid">
        <div className="card hero-card">
          <div className="hero-top">
            <div>
              <div className="eyebrow">DAILY TARGET</div>
              <h2>{fmt(remaining)} <span>kcal left</span></h2>
              <p className="muted">You’ve consumed {fmt(eaten)} kcal of your {fmt(goal)} kcal goal.</p>
            </div>
            <div className="ring" style={{ '--p': `${progress}%` }}>
              <div>{Math.round(progress)}<small>%</small></div>
            </div>
          </div>
          <div className="progress"><span style={{ width: `${progress}%` }} /></div>
          <div className="progress-foot"><span>0 kcal</span><span>{fmt(goal)} kcal</span></div>
        </div>

        <div className="card stat-card">
          <div className="card-head"><div><div className="eyebrow">MACROS</div><h3>Today’s balance</h3></div><span className="mini-icon">⚡</span></div>
          <div className="macro-grid">
            <Macro label="Protein" value={today.protein} unit="g" percent={macroPct(today.protein)} tone="protein" />
            <Macro label="Carbs" value={today.carbs} unit="g" percent={macroPct(today.carbs)} tone="carbs" />
            <Macro label="Fat" value={today.fat} unit="g" percent={macroPct(today.fat)} tone="fat" />
          </div>
        </div>
      </section>

      <section className="two-col">
        <div className="card chart-card">
          <div className="card-head"><div><div className="eyebrow">WEEKLY TREND</div><h3>Calories this week</h3></div><span className="chart-value">{fmt(avg(dashboard.week, 'calories'))}<small> avg</small></span></div>
          <div className="chart"><ResponsiveContainer width="100%" height="100%">
            <AreaChart data={dashboard.week}>
              <defs><linearGradient id="calFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#6d63ff" stopOpacity=".28" /><stop offset="100%" stopColor="#6d63ff" stopOpacity="0" /></linearGradient></defs>
              <CartesianGrid stroke="#edf0f6" vertical={false} />
              <XAxis dataKey="date" tickFormatter={v => new Date(v).toLocaleDateString(undefined, { weekday: 'short' }).slice(0,3)} tickLine={false} axisLine={false} />
              <YAxis tickLine={false} axisLine={false} width={38} />
              <Tooltip labelFormatter={v => labelDate(v, true)} formatter={(v) => [`${fmt(v)} kcal`, 'Intake']} />
              <Area type="monotone" dataKey="calories" stroke="#6d63ff" strokeWidth={3} fill="url(#calFill)" />
            </AreaChart>
          </ResponsiveContainer></div>
        </div>

        <div className="card chart-card">
          <div className="card-head"><div><div className="eyebrow">MONTHLY VIEW</div><h3>Last 30 days</h3></div><span className="chart-value">{fmt(avg(dashboard.month, 'calories'))}<small> avg</small></span></div>
          <div className="chart"><ResponsiveContainer width="100%" height="100%">
            <BarChart data={dashboard.month} barCategoryGap="25%">
              <CartesianGrid stroke="#edf0f6" vertical={false} />
              <XAxis dataKey="date" tickFormatter={v => `${new Date(v).getDate()}`} interval={4} tickLine={false} axisLine={false} />
              <YAxis tickLine={false} axisLine={false} width={38} />
              <Tooltip labelFormatter={v => labelDate(v, true)} formatter={(v) => [`${fmt(v)} kcal`, 'Intake']} />
              <Bar dataKey="calories" radius={[5, 5, 2, 2]} fill="#23b386" />
            </BarChart>
          </ResponsiveContainer></div>
        </div>
      </section>

      <section className="card">
        <div className="card-head">
          <div><div className="eyebrow">TODAY</div><h3>Recent food</h3></div>
          <button className="ghost-button" onClick={() => onNavigate('log')}>+ Add food</button>
        </div>
        {entries.length === 0 ? <EmptyState text="Nothing logged today yet." action="Log your first food" onClick={() => onNavigate('log')} /> :
          <div className="entry-list">{entries.slice(0, 5).map(entry => <EntryRow key={entry.id} entry={entry} onDelete={async () => { await api.deleteEntry(entry.id); load() }} />)}</div>}
      </section>
    </>
  )
}

function Macro({ label, value, unit, percent, tone }) {
  return <div className={`macro macro-${tone}`}>
    <div className="macro-head"><span>{label}</span><strong>{fmt(value, 1)}{unit}</strong></div>
    <div className="tiny-track"><span style={{ width: `${Math.min(percent, 100)}%` }} /></div>
    <small>{percent}% of today’s macro grams</small>
  </div>
}

function LogFood({ onSaved }) {
  const [query, setQuery] = useState('')
  const [foods, setFoods] = useState([])
  const [selected, setSelected] = useState(null)
  const [grams, setGrams] = useState(100)
  const [meal, setMeal] = useState('BREAKFAST')
  const [busy, setBusy] = useState(false)
  const [searching, setSearching] = useState(false)
  const [error, setError] = useState('')

  const doSearch = async (e) => {
    e?.preventDefault()
    if (!query.trim()) return
    setSearching(true); setError(''); setSelected(null)
    try {
      setFoods(await api.searchFoods(query.trim()))
    } catch (err) {
      setError(err.message)
    } finally { setSearching(false) }
  }

  const serving = useMemo(() => {
    if (!selected) return null
    const factor = Number(grams || 0) / 100
    return {
      calories: selected.caloriesPer100g * factor,
      protein: selected.proteinsPer100g * factor,
      carbs: selected.carbsPer100g * factor,
      fat: selected.fatsPer100g * factor,
    }
  }, [selected, grams])

  const add = async () => {
    if (!selected || !serving || Number(grams) <= 0) return
    setBusy(true); setError('')
    try {
      await api.addEntry({
        foodName: selected.name,
        fdcId: selected.fdcId,
        consumedOn: dateKey(),
        mealType: meal,
        servingGrams: Number(grams),
        ...serving,
      })
      onSaved(`${selected.name} added to ${MEALS.find(m => m.key === meal)?.label}.`)
      setSelected(null); setFoods([]); setQuery(''); setGrams(100)
    } catch (err) {
      setError(err.message)
    } finally { setBusy(false) }
  }

  return <>
    <Topbar title="Log food" subtitle="Search real nutrition data and add it to today." />
    <div className="log-grid">
      <section className="card">
        <div className="card-head"><div><div className="eyebrow">USDA FOOD DATABASE</div><h3>Find a food</h3></div><span className="source-pill">USDA</span></div>
        <form className="search-row" onSubmit={doSearch}>
          <input value={query} onChange={e => setQuery(e.target.value)} placeholder="Try “chicken breast”, “banana”, “rice”…" />
          <button className="primary" disabled={searching}>{searching ? 'Searching…' : 'Search'}</button>
        </form>
        {error && <div className="error-box">{error}</div>}
        {foods.length > 0 && <div className="food-results">
          {foods.map(food => <button type="button" key={food.fdcId} className={`food-result ${selected?.fdcId === food.fdcId ? 'selected' : ''}`} onClick={() => setSelected(food)}>
            <div className="food-thumb">{food.name.slice(0, 1)}</div>
            <div className="food-copy"><strong>{food.name}</strong><small>{food.brand} · {food.dataType}</small></div>
            <div className="food-kcal">{fmt(food.caloriesPer100g)}<small>kcal/100g</small></div>
          </button>)}
        </div>}
        {!foods.length && !searching && <div className="soft-empty">Search above to browse USDA nutrition data.</div>}
      </section>

      <section className="card add-card">
        <div className="eyebrow">ADD TO TODAY</div>
        <h3>{selected ? selected.name : 'Select a food'}</h3>
        <p className="muted">{selected ? `${selected.brand} · nutrition values from USDA` : 'Choose a result from the search list to calculate the serving.'}</p>

        <div className="meal-picker">{MEALS.map(m => <button key={m.key} type="button" className={meal === m.key ? 'active' : ''} onClick={() => setMeal(m.key)}>{m.emoji} {m.label}</button>)}</div>

        <label>Serving size (grams)
          <input type="number" min="1" max="5000" value={grams} onChange={e => setGrams(e.target.value)} />
        </label>

        <div className="calc-card">
          <div><span>Calories</span><strong>{fmt(serving?.calories)} kcal</strong></div>
          <div><span>Protein</span><strong>{fmt(serving?.protein, 1)} g</strong></div>
          <div><span>Carbs</span><strong>{fmt(serving?.carbs, 1)} g</strong></div>
          <div><span>Fat</span><strong>{fmt(serving?.fat, 1)} g</strong></div>
        </div>

        <button className="primary large full-button" disabled={!selected || busy} onClick={add}>{busy ? 'Adding…' : 'Add to today'}</button>
      </section>
    </div>
  </>
}

function History() {
  const [dashboard, setDashboard] = useState(null)
  useEffect(() => { api.dashboard().then(setDashboard) }, [])
  if (!dashboard) return <PageLoader />

  return <>
    <Topbar title="History" subtitle="Your recent daily intake at a glance." />
    <section className="card">
      <div className="card-head"><div><div className="eyebrow">DAILY HISTORY</div><h3>Last 30 days</h3></div><div className="legend-dot">● Intake</div></div>
      <div className="history-table">
        {dashboard.month.slice().reverse().map(day => (
          <div className="history-row" key={day.date}>
            <div><strong>{labelDate(day.date, true)}</strong><span>{day.protein.toFixed(1)}g protein · {day.carbs.toFixed(1)}g carbs · {day.fat.toFixed(1)}g fat</span></div>
            <div className="history-bar"><span style={{ width: `${Math.min(day.calories / dashboard.dailyGoal * 100, 100)}%` }} /></div>
            <strong>{fmt(day.calories)} kcal</strong>
          </div>
        ))}
      </div>
    </section>
  </>
}

function Settings({ user, onSaved }) {
  const [name, setName] = useState(user.displayName)
  const [goal, setGoal] = useState(user.dailyGoal)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const save = async (e) => {
    e.preventDefault(); setSaving(true); setError('')
    try {
      onSaved(await api.updateProfile({ displayName: name, dailyGoal: Number(goal) }))
    } catch (err) { setError(err.message) } finally { setSaving(false) }
  }
  return <>
    <Topbar title="Settings" subtitle="Personalize your calorie target." />
    <section className="card settings-card">
      <div className="eyebrow">PROFILE</div>
      <h3>Daily preferences</h3>
      {error && <div className="error-box">{error}</div>}
      <form className="form-stack narrow" onSubmit={save}>
        <label>Display name
          <input required value={name} onChange={e => setName(e.target.value)} />
        </label>
        <label>Daily calorie goal
          <input required type="number" min="500" max="10000" value={goal} onChange={e => setGoal(e.target.value)} />
        </label>
        <label>Email
          <input disabled value={user.email} />
        </label>
        <button className="primary large" disabled={saving}>{saving ? 'Saving…' : 'Save settings'}</button>
      </form>
    </section>
  </>
}

function EntryRow({ entry, onDelete }) {
  const meal = MEALS.find(m => m.key === entry.mealType) || MEALS[3]
  return <div className="entry-row">
    <div className="entry-icon">{meal.emoji}</div>
    <div className="entry-main"><strong>{entry.foodName}</strong><span>{meal.label} · {fmt(entry.servingGrams, 0)} g · {fmt(entry.protein, 1)}g protein</span></div>
    <div className="entry-cal">{fmt(entry.calories)} kcal</div>
    <button className="delete-button" title="Delete" onClick={onDelete}>×</button>
  </div>
}

function EmptyState({ text, action, onClick }) {
  return <div className="empty-state"><div className="empty-icon">＋</div><strong>{text}</strong><button className="ghost-button" onClick={onClick}>{action}</button></div>
}

function Topbar({ title, subtitle }) {
  return <header className="topbar"><div><h1>{title}</h1><p>{subtitle}</p></div><div className="date-chip">{labelDate(dateKey(), true)}</div></header>
}

function PageLoader() { return <div className="page-loader">Loading your nutrition data…</div> }
function avg(list, key) {
  if (!list?.length) return 0
  return list.reduce((s, x) => s + Number(x[key] || 0), 0) / list.length
}

createRoot(document.getElementById('root')).render(<App />)
