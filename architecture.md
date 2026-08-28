# UPI Soundbox
System Architecture Specification

Version: 1.0
Status: Architecture Freeze Pending Approval

Author: Project Architecture Team

---

# 1. EXECUTIVE SUMMARY

## 1.1 Purpose

UPI Soundbox is a privacy-first Android application that announces
incoming UPI payment notifications in real time.

The application acts as a software-only alternative to
merchant soundboxes while maintaining complete local processing.

No financial data shall leave the device.

No cloud infrastructure shall be required.

---

## 1.2 Business Objective

Provide:

- Real-time payment announcements
- Offline operation
- Local processing
- Merchant-friendly usability
- Multi-provider support

while maintaining:

- privacy
- reliability
- low latency
- low battery usage

---

# 2. SYSTEM CONTEXT

## External Systems

Android OS

↓

Notification Framework

↓

UPI Applications

- PhonePe
- Google Pay
- Paytm
- BHIM
- Cred
- Amazon Pay

↓

UPI Soundbox

---

# 3. ARCHITECTURAL PRINCIPLES

## AP-01

Offline First

Internet must never be required.

---

## AP-02

Privacy First

No financial data may leave device.

---

## AP-03

Deterministic Processing

Given identical notification input,
the output must always be identical.

---

## AP-04

Modular Design

Each subsystem must be independently replaceable.

---

## AP-05

Failure Isolation

Subsystem failure must not cascade.

---

## AP-06

ADB First Development

Every implementation must be testable through ADB.

---

## AP-07

Evidence Based Development

All assumptions must be validated using:

- Android documentation
- source code
- device testing
- ADB verification

Never assume undocumented behavior.

---

# 4. C4 MODEL

---

# LEVEL 1
# SYSTEM CONTEXT

+------------------------+
| Android Device         |
+------------------------+

        |

        v

+------------------------+
| UPI Soundbox           |
+------------------------+

        |

        v

+------------------------+
| Android Notification   |
| Framework              |
+------------------------+

        |

        v

+------------------------+
| UPI Applications       |
+------------------------+

---

# LEVEL 2
# CONTAINER DIAGRAM

+-----------------------------------+
| Presentation Layer                |
+-----------------------------------+

+-----------------------------------+
| Domain Layer                      |
+-----------------------------------+

+-----------------------------------+
| Data Layer                        |
+-----------------------------------+

+-----------------------------------+
| Platform Layer                    |
+-----------------------------------+

---

# LEVEL 3
# COMPONENT DIAGRAM

notification-module

↓

parser-module

↓

validation-module

↓

deduplication-module

↓

announcement-module

↓

tts-module

↓

storage-module

---

# LEVEL 4
# CODE STRUCTURE

core/

domain/

data/

notification/

parser/

tts/

storage/

settings/

history/

ui/

debug/

testing/

---

# 5. CLEAN ARCHITECTURE

Presentation

↓

Domain

↓

Data

↓

Framework

Dependency direction must always point inward.

Domain layer must not depend on Android.

---

# 6. MODULE BOUNDARIES

## notification-module

Purpose:

Receive notifications.

Responsibilities:

- capture notifications
- extract metadata
- dispatch events

Must NOT:

- parse business logic
- perform TTS
- access database

---

## parser-module

Purpose:

Extract transaction information.

Responsibilities:

- amount extraction
- sender extraction
- provider detection

Must NOT:

- speak announcements
- access UI

---

## validation-module

Purpose:

Verify transaction legitimacy.

Responsibilities:

- credit detection
- format validation
- mandatory field checks

---

## deduplication-module

Purpose:

Prevent duplicate announcements.

Responsibilities:

- notification fingerprinting
- duplicate detection
- cooldown enforcement

---

## announcement-module

Purpose:

Generate speech payload.

Responsibilities:

- sentence generation
- localization
- formatting

---

## tts-module

Purpose:

Voice output.

Responsibilities:

- queue management
- speech synthesis
- retry handling

---

## storage-module

Purpose:

Persistence.

Responsibilities:

- Room database
- DataStore
- caching

---

# 7. DOMAIN MODEL

Transaction

Announcement

NotificationEvent

ParsedTransaction

ValidatedTransaction

UserSettings

SpeechRequest

SpeechResult

DuplicateCheckResult

---

# 8. DATA FLOW

Notification Posted

↓

Notification Listener

↓

Notification Filter

↓

Parser

↓

Validator

↓

Deduplication

↓

Database Save

↓

Announcement Builder

↓

Speech Queue

↓

TTS

↓

Speaker

---

# 9. SEQUENCE DIAGRAM

User Pays Merchant

↓

UPI App Generates Notification

↓

Android Posts Notification

↓

Notification Listener Receives Event

↓

Parser Extracts Data

↓

Validation Engine Verifies Transaction

↓

Deduplication Checks History

↓

Database Stores Transaction

↓

Announcement Generated

↓

Speech Queue Updated

↓

TTS Speaks Amount

↓

History Updated

---

# 10. NOTIFICATION ENGINE

## Supported Sources

PhonePe

Google Pay

Paytm

BHIM

Cred

Amazon Pay

---

## Provider Architecture

BaseParser

↓

PhonePeParser

GooglePayParser

PaytmParser

BHIMParser

CredParser

AmazonPayParser

---

## Parser Strategy

Provider Specific

↓

Pattern Matching

↓

Regex Extraction

↓

Validation

---

# 11. TRANSACTION DETECTION ENGINE

Credit Keywords

received

credited

payment received

money received

received from

UPI credited

---

Debit Keywords

paid

debited

sent

payment made

transfer completed

---

# 12. DEDUPLICATION ARCHITECTURE

Fingerprint Components

provider

amount

sender

timestamp

notification hash

---

Deduplication Window

Default:

60 seconds

Configurable

---

# 13. ANNOUNCEMENT ARCHITECTURE

Input

₹500

Rahul

PhonePe

↓

Template Engine

↓

Output

Received five hundred rupees from Rahul

---

# 14. TTS ARCHITECTURE

SpeechRequest

↓

Queue Manager

↓

Language Resolver

↓

Voice Resolver

↓

TTS Engine

↓

Audio Output

---

Supported Languages

English

Hindi

Mixed

---

# 15. DATABASE ARCHITECTURE

Room Database

Entities

TransactionEntity

AnnouncementEntity

SettingsEntity

ProviderEntity

---

Indexes

timestamp

provider

amount

transactionType

---

# 16. SETTINGS ARCHITECTURE

DataStore

Fields

language

volume

voice

speed

enabledApps

duplicateWindow

darkMode

---

# 17. BACKGROUND EXECUTION

Foreground Service

↓

Notification Listener

↓

WorkManager

↓

Database Tasks

---

# 18. BATTERY OPTIMIZATION

Requirements

No polling

No background loops

No wake locks unless justified

Event driven architecture only

---

# 19. MEMORY OPTIMIZATION

Target

<150MB

Rules

Avoid large object retention

Avoid memory leaks

Avoid static references

---

# 20. SECURITY ARCHITECTURE

Data Classification

Public

Internal

Sensitive

Financial

---

Financial Data Rules

No cloud upload

No telemetry

No analytics

No crash reporting

No remote logging

No third-party collection

---

# 21. THREAT MODEL

Threats

Notification spoofing

Fake payment notifications

Malicious apps

Permission abuse

Data extraction

Log leakage

---

Mitigations

Provider validation

App whitelist

Notification signature validation

Local-only storage

Encrypted settings

---

# 22. STRIDE ANALYSIS

Spoofing

Tampering

Repudiation

Information Disclosure

Denial Of Service

Elevation Of Privilege

Each threat must have mitigation before release.

---

# 23. OBSERVABILITY ARCHITECTURE

Logging System

Timber

Logcat

ADB

---

Log Levels

DEBUG

INFO

WARN

ERROR

---

No remote logging.

---

# 24. DEBUGGING ARCHITECTURE

Official Workflow

Code

↓

Build

↓

ADB Install

↓

ADB Execute

↓

ADB Log Review

↓

Bug Fix

↓

Retest

↓

Approval

---

Approved Commands

adb logcat

adb shell dumpsys notification

adb shell am

adb shell pm

adb bugreport

adb shell settings

adb shell cmd

---

# 25. OEM COMPATIBILITY

Must be tested on

Pixel

Samsung

Xiaomi

Redmi

Poco

Realme

OnePlus

Vivo

Oppo

IQOO

---

Special Focus

Battery Optimization

Background Restrictions

Notification Delivery

Foreground Service Survival

---

# 26. TESTING REQUIREMENTS

Unit Testing

Integration Testing

UI Testing

ADB Testing

Manual Testing

Regression Testing

---

No phase may advance without approval.

---

# 27. RELEASE CRITERIA

All tests passed

No critical bugs

No memory leaks

No crashes

No parser failures

No duplicate announcements

ADB verification completed

Approval granted

---

# 28. FUTURE ARCHITECTURE

Bluetooth Speaker

WearOS

Multi Device Sync

Merchant Dashboard

Soundbox Hardware

Custom Voice Packs

Remote Monitoring (Optional)

Not included in V1.
