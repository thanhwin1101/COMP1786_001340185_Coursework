// src/screens/HikeListScreen.js

import React, { useEffect, useState, useCallback } from 'react';
import {
  View,
  Text,
  TextInput,
  StyleSheet,
  TouchableOpacity,
  FlatList,
  Alert,
  Platform,
} from 'react-native';
import DateTimePicker from '@react-native-community/datetimepicker';

import {
  getAllHikes,
  deleteHike,
  resetDatabase,     // ✅ dùng lại resetDatabase
} from '../storage/db';

export default function HikeListScreen({ navigation }) {
  const [hikes, setHikes] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [showFilter, setShowFilter] = useState(false);
  const [filterLocation, setFilterLocation] = useState('');
  const [filterMinDistance, setFilterMinDistance] = useState('');
  const [filterDate, setFilterDate] = useState('');

  const [showFilterDatePicker, setShowFilterDatePicker] = useState(false);

  const loadData = useCallback(async () => {
    const data = await getAllHikes();
    setHikes(data);
  }, []);

  useEffect(() => {
    const unsub = navigation.addListener('focus', loadData);
    return unsub;
  }, [navigation, loadData]);

  // 👉 Xoá TẤT CẢ filter + search (không động DB)
  function clearAllFilters() {
    setSearchText('');
    setFilterLocation('');
    setFilterMinDistance('');
    setFilterDate('');
    setShowFilter(false);
  }

  // 👉 Reset DATABASE bằng nút Reset ngoài cùng
  function handleResetDb() {
    Alert.alert(
      'Reset database',
      'This will delete ALL hikes and observations. Continue?',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Reset',
          style: 'destructive',
          onPress: async () => {
            await resetDatabase();
            clearAllFilters();   // xoá luôn filter và đóng panel
            await loadData();
          },
        },
      ]
    );
  }

  function applyFilter(list) {
    let result = list;

    if (searchText.trim()) {
      const keyword = searchText.toLowerCase();
      result = result.filter(h =>
        h.name.toLowerCase().includes(keyword)
      );
    }
    if (filterLocation.trim()) {
      const loc = filterLocation.toLowerCase();
      result = result.filter(h =>
        h.location.toLowerCase().includes(loc)
      );
    }
    if (filterMinDistance.trim()) {
      const min = Number(filterMinDistance);
      if (!isNaN(min)) {
        result = result.filter(h => h.distance >= min);
      }
    }
    if (filterDate.trim()) {
      result = result.filter(h => h.date === filterDate.trim());
    }
    return result;
  }

  const filteredHikes = applyFilter(hikes);

  function handleDelete(hike) {
    Alert.alert(
      'Delete hike',
      `Are you sure you want to delete "${hike.name}"?`,
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: async () => {
            await deleteHike(hike.id);
            await loadData();
          },
        },
      ]
    );
  }

  function onFilterDateChange(event, selectedDate) {
    if (event.type === 'dismissed') {
      setShowFilterDatePicker(false);
      return;
    }
    const d = selectedDate || new Date();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    const yyyy = d.getFullYear();
    const formatted = `${mm}/${dd}/${yyyy}`;
    setFilterDate(formatted);
    setShowFilterDatePicker(false);
  }

  const renderItem = ({ item }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.cardTitle}>{item.name}</Text>
        <View style={styles.badge}>
          <Text style={styles.badgeText}>{item.difficulty}</Text>
        </View>
      </View>

      <Text style={styles.cardSub}>📍 {item.location}</Text>
      <Text style={styles.cardSub}>📅 {item.date}</Text>

      <View style={styles.statsRow}>
        <View style={styles.statBox}>
          <Text style={styles.statLabel}>Distance</Text>
          <Text style={styles.statValue}>{item.distance} km</Text>
        </View>
        <View style={styles.statBox}>
          <Text style={styles.statLabel}>Duration</Text>
          <Text style={styles.statValue}>{item.duration} h</Text>
        </View>
        <View style={styles.statBox}>
          <Text style={styles.statLabel}>Elevation</Text>
          <Text style={styles.statValue}>{item.elevation} m</Text>
        </View>
      </View>

      <View style={styles.cardButtons}>
        <TouchableOpacity
          style={[styles.smallBtn, styles.outlineBtn]}
          onPress={() =>
            navigation.navigate('HikeForm', { mode: 'edit', id: item.id })
          }
        >
          <Text style={styles.outlineBtnText}>Edit</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.smallBtn, styles.dangerBtn]}
          onPress={() => handleDelete(item)}
        >
          <Text style={styles.dangerBtnText}>Delete</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.smallBtn, styles.secondaryBtn]}
          onPress={() =>
            navigation.navigate('Observations', {
              hikeId: item.id,
              hikeName: item.name,
            })
          }
        >
          <Text style={styles.secondaryBtnText}>Obs</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      {/* top bar */}
      <View style={styles.topBar}>
        <Text style={styles.title}>My Hikes</Text>
        <TouchableOpacity
          style={styles.addBtn}
          onPress={() => navigation.navigate('HikeForm', { mode: 'add' })}
        >
          <Text style={styles.addBtnText}>+ Add</Text>
        </TouchableOpacity>
      </View>

      {/* search row */}
      <View style={styles.searchRow}>
        <TextInput
          style={styles.searchInput}
          placeholder="Search by name"
          value={searchText}
          onChangeText={setSearchText}
        />
        <TouchableOpacity
          style={styles.filterBtn}
          onPress={() => setShowFilter(prev => !prev)}
        >
          <Text style={styles.filterBtnText}>Filter</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.filterBtn}
          onPress={handleResetDb}          // ✅ Reset DB bằng nút Reset này
        >
          <Text style={styles.filterBtnText}>Reset</Text>
        </TouchableOpacity>
      </View>

      {/* advanced filter */}
      {showFilter && (
        <View style={styles.filterPanel}>
          <TextInput
            style={styles.filterInput}
            placeholder="Location"
            value={filterLocation}
            onChangeText={setFilterLocation}
          />
          <TextInput
            style={styles.filterInput}
            placeholder="Min distance (km)"
            keyboardType="numeric"
            value={filterMinDistance}
            onChangeText={setFilterMinDistance}
          />

          {/* Date với DatePicker */}
          <TouchableOpacity
            onPress={() => setShowFilterDatePicker(true)}
          >
            <View pointerEvents="none">
              <TextInput
                style={styles.filterInput}
                placeholder="Date (MM/DD/YYYY)"
                value={filterDate}
                editable={false}
              />
            </View>
          </TouchableOpacity>

          {/* Nút xoá filter trong panel */}
          <TouchableOpacity
            style={styles.resetDbBtn}
            onPress={clearAllFilters}
          >
            <Text style={styles.resetDbText}>Clear all filters</Text>
          </TouchableOpacity>
        </View>
      )}

      <FlatList
        style={styles.list}
        data={filteredHikes}
        keyExtractor={item => item.id.toString()}
        renderItem={renderItem}
        ListEmptyComponent={
          <Text style={styles.emptyText}>No hikes yet. Add one!</Text>
        }
      />

      {/* DateTimePicker cho filter date */}
      {showFilterDatePicker && (
        <DateTimePicker
          value={filterDate ? new Date(filterDate) : new Date()}
          mode="date"
          display={Platform.OS === 'ios' ? 'spinner' : 'default'}
          onChange={onFilterDateChange}
        />
      )}
    </View>
  );
}

const PURPLE = '#6C47FF';
const DANGER = '#DC3545';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F1F2F6',
    paddingHorizontal: 12,
    paddingTop: 24,
  },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  title: {
    flex: 1,
    fontSize: 22,
    fontWeight: 'bold',
  },
  addBtn: {
    backgroundColor: PURPLE,
    paddingHorizontal: 18,
    paddingVertical: 8,
    borderRadius: 20,
  },
  addBtnText: {
    color: '#fff',
    fontWeight: '600',
  },
  searchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  searchInput: {
    flex: 1,
    borderBottomWidth: 1,
    borderColor: '#999',
    paddingVertical: 4,
    paddingHorizontal: 4,
    marginRight: 8,
  },
  filterBtn: {
    backgroundColor: PURPLE,
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 20,
    marginLeft: 4,
  },
  filterBtnText: {
    color: '#fff',
    fontWeight: '500',
  },
  filterPanel: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 12,
    marginBottom: 8,
  },
  filterInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    paddingHorizontal: 8,
    paddingVertical: 6,
    marginBottom: 8,
  },
  resetDbBtn: {
    alignSelf: 'flex-start',
    backgroundColor: '#333',
    borderRadius: 20,
    paddingHorizontal: 14,
    paddingVertical: 6,
    marginTop: 4,
  },
  resetDbText: {
    color: '#fff',
    fontWeight: '500',
  },
  list: {
    marginTop: 4,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 12,
    marginVertical: 6,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 4,
  },
  cardTitle: {
    flex: 1,
    fontSize: 16,
    fontWeight: 'bold',
  },
  badge: {
    backgroundColor: '#F5A623',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
  },
  badgeText: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 12,
  },
  cardSub: {
    fontSize: 13,
    color: '#555',
  },
  statsRow: {
    flexDirection: 'row',
    marginTop: 8,
  },
  statBox: {
    flex: 1,
    backgroundColor: '#F6F7FB',
    borderRadius: 8,
    paddingVertical: 6,
    alignItems: 'center',
    marginHorizontal: 3,
  },
  statLabel: {
    fontSize: 11,
    color: '#777',
  },
  statValue: {
    fontSize: 13,
    fontWeight: '600',
  },
  cardButtons: {
    flexDirection: 'row',
    marginTop: 10,
    justifyContent: 'space-between',
  },
  smallBtn: {
    flex: 1,
    paddingVertical: 6,
    marginHorizontal: 3,
    borderRadius: 20,
    alignItems: 'center',
  },
  outlineBtn: {
    borderWidth: 1,
    borderColor: '#333',
  },
  outlineBtnText: {
    color: '#333',
    fontWeight: '500',
  },
  dangerBtn: {
    backgroundColor: DANGER,
  },
  dangerBtnText: {
    color: '#fff',
    fontWeight: '600',
  },
  secondaryBtn: {
    backgroundColor: '#495057',
  },
  secondaryBtnText: {
    color: '#fff',
    fontWeight: '600',
  },
  emptyText: {
    textAlign: 'center',
    marginTop: 40,
    color: '#777',
  },
}); 