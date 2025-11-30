// src/storage/db.js
import * as SQLite from 'expo-sqlite';

let dbPromise = null;

async function getDb() {
  if (!dbPromise) {
    dbPromise = SQLite.openDatabaseAsync('mhike.db');
  }
  return dbPromise;
}

// Tạo bảng nếu chưa có
export async function initDatabase() {
  const db = await getDb();

  await db.execAsync(`
    PRAGMA foreign_keys = ON;

    CREATE TABLE IF NOT EXISTS hikes (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      location TEXT NOT NULL,
      date TEXT NOT NULL,
      hasParking INTEGER NOT NULL,
      distance REAL NOT NULL,
      duration REAL NOT NULL,
      elevation INTEGER NOT NULL,
      difficulty TEXT NOT NULL,
      groupSize INTEGER NOT NULL,
      terrain TEXT,
      description TEXT
    );

    CREATE TABLE IF NOT EXISTS observations (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      hikeId INTEGER NOT NULL,
      title TEXT NOT NULL,
      time TEXT NOT NULL,
      comment TEXT,
      FOREIGN KEY(hikeId) REFERENCES hikes(id) ON DELETE CASCADE
    );
  `);
}

// ===== Hike CRUD =====
export async function getAllHikes() {
  const db = await getDb();
  const rows = await db.getAllAsync('SELECT * FROM hikes ORDER BY date DESC');
  return rows;
}

export async function getHikeById(id) {
  const db = await getDb();
  const hike = await db.getFirstAsync('SELECT * FROM hikes WHERE id = ?', [id]);
  return hike;
}

export async function insertHike(hike) {
  const db = await getDb();
  const result = await db.runAsync(
    `INSERT INTO hikes
    (name, location, date, hasParking, distance, duration, elevation,
     difficulty, groupSize, terrain, description)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [
      hike.name,
      hike.location,
      hike.date,
      hike.hasParking ? 1 : 0,
      hike.distance,
      hike.duration,
      hike.elevation,
      hike.difficulty,
      hike.groupSize,
      hike.terrain,
      hike.description,
    ]
  );
  return result.lastInsertRowId;
}

export async function updateHike(hike) {
  const db = await getDb();
  await db.runAsync(
    `UPDATE hikes SET
      name = ?, location = ?, date = ?, hasParking = ?,
      distance = ?, duration = ?, elevation = ?,
      difficulty = ?, groupSize = ?, terrain = ?, description = ?
     WHERE id = ?`,
    [
      hike.name,
      hike.location,
      hike.date,
      hike.hasParking ? 1 : 0,
      hike.distance,
      hike.duration,
      hike.elevation,
      hike.difficulty,
      hike.groupSize,
      hike.terrain,
      hike.description,
      hike.id,
    ]
  );
}

export async function deleteHike(id) {
  const db = await getDb();
  await db.runAsync('DELETE FROM hikes WHERE id = ?', [id]);
}

export async function resetDatabase() {
  const db = await getDb();
  await db.execAsync(`
    DELETE FROM observations;
    DELETE FROM hikes;
    VACUUM;
  `);
}

// ===== Observations =====
export async function getObservationsByHike(hikeId) {
  const db = await getDb();
  return await db.getAllAsync(
    'SELECT * FROM observations WHERE hikeId = ? ORDER BY time DESC',
    [hikeId]
  );
}

export async function insertObservation(obs) {
  const db = await getDb();
  const result = await db.runAsync(
    `INSERT INTO observations (hikeId, title, time, comment)
     VALUES (?, ?, ?, ?)`,
    [obs.hikeId, obs.title, obs.time, obs.comment]
  );
  return result.lastInsertRowId;
}

export async function updateObservation(obs) {
  const db = await getDb();
  await db.runAsync(
    `UPDATE observations SET title = ?, time = ?, comment = ?
     WHERE id = ?`,
    [obs.title, obs.time, obs.comment, obs.id]
  );
}

export async function deleteObservation(id) {
  const db = await getDb();
  await db.runAsync('DELETE FROM observations WHERE id = ?', [id]);
}
