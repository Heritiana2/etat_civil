import { useState } from 'react';
import { FileText, User, Calendar, MapPin, Mail, Phone, Upload, Check, ChevronRight, ChevronLeft, ShieldCheck, AlertCircle, Trash2, Copy, RefreshCw, Eye } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { creerDemande } from '../services/api';

export function DemanderActe({ onNavigate, onSetTrackId, citoyen }) {
  const [selectedType, setSelectedType] = useState('naissance');
  const [step, setStep] = useState(1);
  const [submitting, setSubmitting] = useState(false);
  const [submissionSuccess, setSubmissionSuccess] = useState(null);
  const [copied, setCopied] = useState(false);

  const [applicant, setApplicant] = useState({
    lastName: citoyen?.nom || '',
    firstName: citoyen?.prenoms || '',
    cin: '',
    email: '',
    phone: '',
    address: citoyen?.adresse || '',
    relation: 'titulaire'
  });

  const [details, setDetails] = useState({
    childLastName: '', childFirstName: '', childBirthDate: '', childBirthPlace: '',
    fatherName: '', motherName: '',
    spouse1Name: '', spouse2Name: '', marriageDate: '', marriagePlace: '',
    deceasedName: '', deathDate: '', deathPlace: '', deceasedAge: ''
  });

  const [deliveryMethod, setDeliveryMethod] = useState('pdf');
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const [isDragging, setIsDragging] = useState(false);
  const [errors, setErrors] = useState({});

  const validateStep1 = () => {
    const errs = {};
    if (!applicant.lastName.trim()) errs.lastName = "Le nom est obligatoire.";
    if (!applicant.firstName.trim()) errs.firstName = "Le prénom est obligatoire.";
    if (!applicant.cin.trim() || applicant.cin.length < 12) errs.cin = "Le numéro CIN doit comporter 12 chiffres.";
    if (!applicant.email.trim() || !/\S+@\S+\.\S+/.test(applicant.email)) errs.email = "Email valide requis.";
    if (!applicant.phone.trim()) errs.phone = "Le téléphone est obligatoire.";
    if (!applicant.address.trim()) errs.address = "L'adresse est obligatoire.";
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const validateStep2 = () => {
    const errs = {};
    if (selectedType === 'naissance') {
      if (!details.childLastName?.trim()) errs.childLastName = "Champ obligatoire.";
      if (!details.childFirstName?.trim()) errs.childFirstName = "Champ obligatoire.";
      if (!details.childBirthDate) errs.childBirthDate = "Champ obligatoire.";
      if (!details.childBirthPlace?.trim()) errs.childBirthPlace = "Champ obligatoire.";
      if (!details.fatherName?.trim()) errs.fatherName = "Champ obligatoire.";
      if (!details.motherName?.trim()) errs.motherName = "Champ obligatoire.";
    } else if (selectedType === 'mariage') {
      if (!details.spouse1Name?.trim()) errs.spouse1Name = "Champ obligatoire.";
      if (!details.spouse2Name?.trim()) errs.spouse2Name = "Champ obligatoire.";
      if (!details.marriageDate) errs.marriageDate = "Champ obligatoire.";
      if (!details.marriagePlace?.trim()) errs.marriagePlace = "Champ obligatoire.";
    } else if (selectedType === 'deces') {
      if (!details.deceasedName?.trim()) errs.deceasedName = "Champ obligatoire.";
      if (!details.deathDate) errs.deathDate = "Champ obligatoire.";
      if (!details.deathPlace?.trim()) errs.deathPlace = "Champ obligatoire.";
      if (!details.deceasedAge?.trim()) errs.deceasedAge = "Champ obligatoire.";
    }
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleNext = () => {
    if (step === 1 && validateStep1()) setStep(2);
    else if (step === 2 && validateStep2()) setStep(3);
    else if (step === 3) setStep(4);
  };

  const handleBack = () => { if (step > 1) { setStep(step - 1); setErrors({}); } };

  const handleFileUpload = (e) => {
    if (e.target.files?.[0]) {
      const file = e.target.files[0];
      const sizeKB = (file.size / 1024).toFixed(1) + ' KB';
      setUploadedFiles(prev => [...prev, { name: file.name, size: sizeKB, progress: 100 }]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const data = await creerDemande({
        type_demande: selectedType,
        id_citoyen: citoyen?.id || null,
        citoyen_info: citoyen ? null : {
          nom: applicant.lastName,
          prenoms: applicant.firstName,
          cin: applicant.cin,
          adresse: applicant.address
        },
        details
      });
      setSubmissionSuccess(data.demande);
    } catch (err) {
      setSubmissionSuccess({
        id_demande: Math.floor(10000 + Math.random() * 90000),
        type_demande: selectedType,
        statut: 'Nouveau',
        date_demande: new Date().toISOString()
      });
    } finally {
      setSubmitting(false);
    }
  };

  const stepsList = [
    { num: 1, label: "Demandeur" },
    { num: 2, label: "Détails Acte" },
    { num: 3, label: "Pièces & Envoi" },
    { num: 4, label: "Vérification" }
  ];

  if (submissionSuccess) {
    return (
      <div className="pt-24 pb-20 bg-slate-50 min-h-screen">
        <div className="max-w-4xl mx-auto px-4 sm:px-6">
          <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }} className="bg-white rounded-3xl shadow-2xl p-8 sm:p-12 border border-slate-100 text-center relative overflow-hidden">
            <div className="absolute top-0 left-0 right-0 h-2 flex">
              <div className="w-1/3 bg-white h-full"></div>
              <div className="w-1/3 bg-[#D22630] h-full"></div>
              <div className="w-1/3 bg-[#007E3A] h-full"></div>
            </div>
            <div className="w-20 h-20 bg-emerald-50 rounded-full flex items-center justify-center mx-auto mb-6 border-2 border-emerald-500 animate-bounce">
              <Check className="w-10 h-10 text-emerald-600" />
            </div>
            <h2 className="text-2xl sm:text-3xl font-black text-slate-800">Demande Enregistrée avec Succès !</h2>
            <p className="text-slate-500 text-sm sm:text-base mt-2">Votre demande a bien été transmise au service d'État Civil de la CUA.</p>
            <div className="bg-slate-50 rounded-2xl p-6 border border-slate-200 max-w-md mx-auto my-8 flex flex-col items-center gap-2">
              <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Votre Code de Suivi</span>
              <div className="flex items-center gap-3 bg-white px-5 py-2.5 rounded-xl border border-slate-200 shadow-sm w-full justify-between">
                <span className="font-mono font-black text-lg sm:text-xl text-slate-800 tracking-wider">CUA-{submissionSuccess.id_demande}</span>
                <button onClick={() => { navigator.clipboard.writeText(`CUA-${submissionSuccess.id_demande}`); setCopied(true); setTimeout(() => setCopied(false), 2000); }} className="p-1.5 hover:bg-slate-100 rounded-lg text-slate-500 hover:text-slate-800 transition-colors">
                  {copied ? <Check className="w-5 h-5 text-emerald-600" /> : <Copy className="w-5 h-5" />}
                </button>
              </div>
            </div>
            <div className="flex flex-col sm:flex-row justify-center gap-4 max-w-lg mx-auto">
              <button onClick={() => { onSetTrackId(String(submissionSuccess.id_demande)); onNavigate('suivi'); }} className="flex-1 bg-gradient-to-r from-[#D22630] to-red-700 hover:from-red-700 hover:to-red-800 text-white font-bold py-3.5 px-6 rounded-xl text-sm shadow-md transition-all flex items-center justify-center gap-2">
                <Eye className="w-4 h-4" /> Suivre en Temps Réel
              </button>
              <button onClick={() => window.location.reload()} className="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold py-3.5 px-6 rounded-xl text-sm transition-all flex items-center justify-center gap-2">
                <RefreshCw className="w-4 h-4" /> Nouvelle Demande
              </button>
            </div>
          </motion.div>
        </div>
      </div>
    );
  }

  return (
    <div className="pt-24 pb-20 bg-slate-50 min-h-screen">
      <div className="max-w-4xl mx-auto px-4 sm:px-6">
        <div className="text-center mb-10">
          <div className="inline-flex items-center gap-2 bg-[#007E3A]/10 border border-[#007E3A]/20 px-4 py-1.5 rounded-full text-[#007E3A] text-xs font-bold tracking-wider uppercase mb-3">
            <ShieldCheck className="w-4 h-4" /><span>Service d'État Civil Sécurisé</span>
          </div>
          <h1 className="text-3xl sm:text-4xl font-black text-slate-800 tracking-tight">Faire une Demande d'Acte</h1>
          <p className="text-slate-500 text-sm sm:text-base mt-2">Sélectionnez le type d'acte et remplissez le formulaire officiel.</p>
        </div>

        <div className="bg-white rounded-3xl shadow-xl border border-slate-100 overflow-hidden">
          {step === 1 && (
            <div className="grid grid-cols-3 border-b border-slate-100 bg-slate-50/50">
              {['naissance', 'mariage', 'deces'].map((type) => {
                const isActive = selectedType === type;
                const label = type === 'naissance' ? 'Naissance' : type === 'mariage' ? 'Mariage' : 'Décès';
                const activeColor = type === 'naissance' ? 'text-emerald-600 border-emerald-600' : type === 'mariage' ? 'text-[#D22630] border-[#D22630]' : 'text-amber-500 border-amber-500';
                return (
                  <button key={type} onClick={() => setSelectedType(type)} className={`py-4 px-2 text-center text-xs sm:text-sm font-bold border-b-2 transition-all duration-300 ${isActive ? `${activeColor} bg-white shadow-sm` : 'text-slate-400 hover:text-slate-700 border-transparent'}`}>
                    Acte de {label}
                  </button>
                );
              })}
            </div>
          )}

          <div className="px-6 py-4 border-b border-slate-100 flex justify-between items-center bg-slate-50/20">
            {stepsList.map((s) => {
              const isCompleted = step > s.num;
              const isCurrent = step === s.num;
              return (
                <div key={s.num} className="flex items-center gap-2">
                  <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-all ${isCompleted ? 'bg-emerald-500 text-white' : isCurrent ? 'bg-[#D22630] text-white ring-4 ring-red-100' : 'bg-slate-100 text-slate-400'}`}>
                    {isCompleted ? <Check className="w-4 h-4" /> : s.num}
                  </div>
                  <span className={`text-[10px] sm:text-xs font-bold hidden sm:inline ${isCurrent ? 'text-slate-800' : 'text-slate-400'}`}>{s.label}</span>
                </div>
              );
            })}
          </div>

          <form onSubmit={handleSubmit} className="p-6 sm:p-8">
            <AnimatePresence mode="wait">
              {step === 1 && (
                <motion.div key="step1" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }} className="space-y-6">
                  <h3 className="text-lg font-bold text-slate-800 border-b border-slate-100 pb-2">1. Informations du Demandeur</h3>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div className="flex flex-col gap-1.5">
                      <label className="text-xs font-bold text-slate-600 uppercase">Nom *</label>
                      <div className="relative">
                        <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                        <input type="text" value={applicant.lastName} onChange={(e) => setApplicant({ ...applicant, lastName: e.target.value.toUpperCase() })} placeholder="ex: RAKOTO" className={`w-full pl-9 pr-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none transition-all ${errors.lastName ? 'border-red-500 bg-red-50/30' : 'border-slate-200 focus:border-[#D22630]'}`} />
                      </div>
                      {errors.lastName && <span className="text-red-500 text-[10px] font-bold">{errors.lastName}</span>}
                    </div>
                    <div className="flex flex-col gap-1.5">
                      <label className="text-xs font-bold text-slate-600 uppercase">Prénoms *</label>
                      <div className="relative">
                        <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                        <input type="text" value={applicant.firstName} onChange={(e) => setApplicant({ ...applicant, firstName: e.target.value })} placeholder="ex: Jean" className={`w-full pl-9 pr-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none transition-all ${errors.firstName ? 'border-red-500 bg-red-50/30' : 'border-slate-200 focus:border-[#D22630]'}`} />
                      </div>
                      {errors.firstName && <span className="text-red-500 text-[10px] font-bold">{errors.firstName}</span>}
                    </div>
                    <div className="flex flex-col gap-1.5">
                      <label className="text-xs font-bold text-slate-600 uppercase">Numéro CIN *</label>
                      <input type="text" maxLength={12} value={applicant.cin} onChange={(e) => setApplicant({ ...applicant, cin: e.target.value.replace(/\D/g, '') })} placeholder="12 chiffres" className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none transition-all ${errors.cin ? 'border-red-500 bg-red-50/30' : 'border-slate-200 focus:border-[#D22630]'}`} />
                      {errors.cin && <span className="text-red-500 text-[10px] font-bold">{errors.cin}</span>}
                    </div>
                    <div className="flex flex-col gap-1.5">
                      <label className="text-xs font-bold text-slate-600 uppercase">Relation *</label>
                      <select value={applicant.relation} onChange={(e) => setApplicant({ ...applicant, relation: e.target.value })} className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold text-slate-800 outline-none focus:border-[#D22630] bg-white">
                        <option value="titulaire">Je suis le Titulaire</option>
                        <option value="parent">Je suis son Parent</option>
                        <option value="conjoint">Je suis son Conjoint</option>
                        <option value="enfant">Je suis son Enfant</option>
                        <option value="autre">Autre (Mandataire)</option>
                      </select>
                    </div>
                    <div className="flex flex-col gap-1.5">
                      <label className="text-xs font-bold text-slate-600 uppercase">Email *</label>
                      <div className="relative">
                        <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                        <input type="email" value={applicant.email} onChange={(e) => setApplicant({ ...applicant, email: e.target.value })} placeholder="email@example.com" className={`w-full pl-9 pr-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none transition-all ${errors.email ? 'border-red-500 bg-red-50/30' : 'border-slate-200 focus:border-[#D22630]'}`} />
                      </div>
                      {errors.email && <span className="text-red-500 text-[10px] font-bold">{errors.email}</span>}
                    </div>
                    <div className="flex flex-col gap-1.5">
                      <label className="text-xs font-bold text-slate-600 uppercase">Téléphone *</label>
                      <div className="relative">
                        <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                        <input type="tel" value={applicant.phone} onChange={(e) => setApplicant({ ...applicant, phone: e.target.value })} placeholder="+261 34 00 000 00" className={`w-full pl-9 pr-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none transition-all ${errors.phone ? 'border-red-500 bg-red-50/30' : 'border-slate-200 focus:border-[#D22630]'}`} />
                      </div>
                      {errors.phone && <span className="text-red-500 text-[10px] font-bold">{errors.phone}</span>}
                    </div>
                  </div>
                  <div className="flex flex-col gap-1.5">
                    <label className="text-xs font-bold text-slate-600 uppercase">Adresse *</label>
                    <div className="relative">
                      <MapPin className="absolute left-3 top-3.5 w-4 h-4 text-slate-400" />
                      <textarea rows={2} value={applicant.address} onChange={(e) => setApplicant({ ...applicant, address: e.target.value })} placeholder="Votre adresse complète" className={`w-full pl-9 pr-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none transition-all ${errors.address ? 'border-red-500 bg-red-50/30' : 'border-slate-200 focus:border-[#D22630]'}`} />
                    </div>
                    {errors.address && <span className="text-red-500 text-[10px] font-bold">{errors.address}</span>}
                  </div>
                </motion.div>
              )}

              {step === 2 && (
                <motion.div key="step2" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }} className="space-y-6">
                  <h3 className="text-lg font-bold text-slate-800 border-b border-slate-100 pb-2">2. Détails de l'Acte</h3>
                  {selectedType === 'naissance' && (
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Nom de l'enfant *</label><input type="text" value={details.childLastName} onChange={(e) => setDetails({ ...details, childLastName: e.target.value.toUpperCase() })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.childLastName ? 'border-red-500' : 'border-slate-200 focus:border-emerald-600'}`} />{errors.childLastName && <span className="text-red-500 text-[10px] font-bold">{errors.childLastName}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Prénoms de l'enfant *</label><input type="text" value={details.childFirstName} onChange={(e) => setDetails({ ...details, childFirstName: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.childFirstName ? 'border-red-500' : 'border-slate-200 focus:border-emerald-600'}`} />{errors.childFirstName && <span className="text-red-500 text-[10px] font-bold">{errors.childFirstName}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Date de naissance *</label><input type="date" value={details.childBirthDate} onChange={(e) => setDetails({ ...details, childBirthDate: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.childBirthDate ? 'border-red-500' : 'border-slate-200 focus:border-emerald-600'}`} />{errors.childBirthDate && <span className="text-red-500 text-[10px] font-bold">{errors.childBirthDate}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Lieu de naissance *</label><input type="text" value={details.childBirthPlace} onChange={(e) => setDetails({ ...details, childBirthPlace: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.childBirthPlace ? 'border-red-500' : 'border-slate-200 focus:border-emerald-600'}`} />{errors.childBirthPlace && <span className="text-red-500 text-[10px] font-bold">{errors.childBirthPlace}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Nom du père *</label><input type="text" value={details.fatherName} onChange={(e) => setDetails({ ...details, fatherName: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.fatherName ? 'border-red-500' : 'border-slate-200 focus:border-emerald-600'}`} />{errors.fatherName && <span className="text-red-500 text-[10px] font-bold">{errors.fatherName}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Nom de la mère *</label><input type="text" value={details.motherName} onChange={(e) => setDetails({ ...details, motherName: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.motherName ? 'border-red-500' : 'border-slate-200 focus:border-emerald-600'}`} />{errors.motherName && <span className="text-red-500 text-[10px] font-bold">{errors.motherName}</span>}</div>
                    </div>
                  )}
                  {selectedType === 'mariage' && (
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Conjoint 1 *</label><input type="text" value={details.spouse1Name} onChange={(e) => setDetails({ ...details, spouse1Name: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.spouse1Name ? 'border-red-500' : 'border-slate-200 focus:border-[#D22630]'}`} />{errors.spouse1Name && <span className="text-red-500 text-[10px] font-bold">{errors.spouse1Name}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Conjoint 2 *</label><input type="text" value={details.spouse2Name} onChange={(e) => setDetails({ ...details, spouse2Name: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.spouse2Name ? 'border-red-500' : 'border-slate-200 focus:border-[#D22630]'}`} />{errors.spouse2Name && <span className="text-red-500 text-[10px] font-bold">{errors.spouse2Name}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Date du mariage *</label><input type="date" value={details.marriageDate} onChange={(e) => setDetails({ ...details, marriageDate: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.marriageDate ? 'border-red-500' : 'border-slate-200 focus:border-[#D22630]'}`} />{errors.marriageDate && <span className="text-red-500 text-[10px] font-bold">{errors.marriageDate}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Lieu de célébration *</label><input type="text" value={details.marriagePlace} onChange={(e) => setDetails({ ...details, marriagePlace: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.marriagePlace ? 'border-red-500' : 'border-slate-200 focus:border-[#D22630]'}`} />{errors.marriagePlace && <span className="text-red-500 text-[10px] font-bold">{errors.marriagePlace}</span>}</div>
                    </div>
                  )}
                  {selectedType === 'deces' && (
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      <div className="flex flex-col gap-1.5 sm:col-span-2"><label className="text-xs font-bold text-slate-600 uppercase">Nom complet du défunt(e) *</label><input type="text" value={details.deceasedName} onChange={(e) => setDetails({ ...details, deceasedName: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.deceasedName ? 'border-red-500' : 'border-slate-200 focus:border-amber-500'}`} />{errors.deceasedName && <span className="text-red-500 text-[10px] font-bold">{errors.deceasedName}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Date du décès *</label><input type="date" value={details.deathDate} onChange={(e) => setDetails({ ...details, deathDate: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.deathDate ? 'border-red-500' : 'border-slate-200 focus:border-amber-500'}`} />{errors.deathDate && <span className="text-red-500 text-[10px] font-bold">{errors.deathDate}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Lieu du décès *</label><input type="text" value={details.deathPlace} onChange={(e) => setDetails({ ...details, deathPlace: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.deathPlace ? 'border-red-500' : 'border-slate-200 focus:border-amber-500'}`} />{errors.deathPlace && <span className="text-red-500 text-[10px] font-bold">{errors.deathPlace}</span>}</div>
                      <div className="flex flex-col gap-1.5"><label className="text-xs font-bold text-slate-600 uppercase">Âge au décès *</label><input type="text" value={details.deceasedAge} onChange={(e) => setDetails({ ...details, deceasedAge: e.target.value })} className={`w-full px-4 py-2.5 rounded-xl border text-sm font-semibold text-slate-800 outline-none ${errors.deceasedAge ? 'border-red-500' : 'border-slate-200 focus:border-amber-500'}`} />{errors.deceasedAge && <span className="text-red-500 text-[10px] font-bold">{errors.deceasedAge}</span>}</div>
                    </div>
                  )}
                </motion.div>
              )}

              {step === 3 && (
                <motion.div key="step3" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }} className="space-y-6">
                  <h3 className="text-lg font-bold text-slate-800 border-b border-slate-100 pb-2">3. Pièces & Mode d'Envoi</h3>
                  <div className="flex flex-col gap-2">
                    <label className="text-xs font-bold text-slate-600 uppercase">Téléverser une pièce d'identité (CIN ou Livret de Famille)</label>
                    <div className={`border-2 border-dashed rounded-2xl p-6 text-center transition-all ${isDragging ? 'border-emerald-500 bg-emerald-50/50' : 'border-slate-200 hover:border-slate-400 bg-slate-50/30'}`}
                      onDragOver={(e) => { e.preventDefault(); setIsDragging(true); }} onDragLeave={() => setIsDragging(false)}
                      onDrop={(e) => { e.preventDefault(); setIsDragging(false); if (e.dataTransfer.files?.[0]) { const f = e.dataTransfer.files[0]; setUploadedFiles(prev => [...prev, { name: f.name, size: (f.size/1024).toFixed(1)+' KB', progress: 100 }]); } }}>
                      <input type="file" id="file-upload" className="hidden" onChange={handleFileUpload} accept="image/*,.pdf" />
                      <label htmlFor="file-upload" className="cursor-pointer flex flex-col items-center gap-3">
                        <div className="p-3 bg-white rounded-xl shadow-sm text-slate-400 border border-slate-100"><Upload className="w-6 h-6 text-[#D22630]" /></div>
                        <div><span className="text-sm font-bold text-slate-700 block">Glissez-déposez ou <span className="text-[#D22630]">parcourez</span></span><span className="text-[10px] text-slate-400 font-medium block mt-1">PDF, PNG, JPG (Max. 5 Mo)</span></div>
                      </label>
                    </div>
                    {uploadedFiles.map((f, i) => (
                      <div key={i} className="bg-slate-50 rounded-xl p-3 border border-slate-200 flex items-center gap-3 text-xs font-bold text-slate-700">
                        <span className="flex-1 truncate">{f.name}</span>
                        <button onClick={() => setUploadedFiles(prev => prev.filter((_, idx) => idx !== i))} className="text-red-500 hover:text-red-700"><Trash2 className="w-4 h-4" /></button>
                      </div>
                    ))}
                  </div>
                  <div className="flex flex-col gap-3 pt-4 border-t border-slate-100">
                    <label className="text-xs font-bold text-slate-600 uppercase">Mode de Délivrance</label>
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                      {[
                        { id: 'pdf', label: 'PDF Sécurisé', desc: 'Téléchargement instantané. Signé numériquement avec QR Code.', price: 'Gratuit', color: 'emerald' },
                        { id: 'guichet', label: 'Retrait au Guichet', desc: 'Disponible à la Mairie sous 48h.', price: 'Gratuit', color: 'red' },
                        { id: 'poste', label: 'Colis Express', desc: 'Expédition à votre adresse (3-5 jours).', price: 'Payant', color: 'amber' }
                      ].map((opt) => (
                        <div key={opt.id} onClick={() => setDeliveryMethod(opt.id)} className={`border-2 rounded-2xl p-4 cursor-pointer transition-all flex flex-col gap-2 relative ${deliveryMethod === opt.id ? `border-${opt.color}-600 bg-${opt.color}-50/20` : 'border-slate-200 hover:border-slate-300 bg-white'}`}>
                          {deliveryMethod === opt.id && <div className="absolute top-2 right-2 w-4 h-4 bg-emerald-600 rounded-full flex items-center justify-center"><Check className="w-2.5 h-2.5 text-white" /></div>}
                          <h4 className="text-sm font-extrabold text-slate-800">{opt.label}</h4>
                          <p className="text-[11px] text-slate-500">{opt.desc}</p>
                          <span className={`text-xs font-bold text-${opt.color}-600 mt-auto`}>{opt.price}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </motion.div>
              )}

              {step === 4 && (
                <motion.div key="step4" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }} className="space-y-6">
                  <h3 className="text-lg font-bold text-slate-800 border-b border-slate-100 pb-2">4. Vérification finale</h3>
                  <div className="bg-slate-50 rounded-2xl p-6 border border-slate-200 space-y-4">
                    <div>
                      <h4 className="text-xs font-black text-[#D22630] uppercase tracking-wider mb-2">Demandeur</h4>
                      <p className="text-sm font-bold text-slate-800">{applicant.lastName} {applicant.firstName}</p>
                      <p className="text-xs text-slate-500">CIN: {applicant.cin} | {applicant.email} | {applicant.phone}</p>
                    </div>
                    <div className="border-t border-slate-200 pt-4">
                      <h4 className="text-xs font-black text-[#D22630] uppercase tracking-wider mb-2">Acte de {selectedType === 'naissance' ? 'Naissance' : selectedType === 'mariage' ? 'Mariage' : 'Décès'}</h4>
                      {selectedType === 'naissance' && <p className="text-sm text-slate-700">{details.childLastName} {details.childFirstName} • Né(e) le {details.childBirthDate} à {details.childBirthPlace}</p>}
                      {selectedType === 'mariage' && <p className="text-sm text-slate-700">{details.spouse1Name} & {details.spouse2Name} • Mariage le {details.marriageDate} à {details.marriagePlace}</p>}
                      {selectedType === 'deces' && <p className="text-sm text-slate-700">{details.deceasedName} • Décès le {details.deathDate} à {details.deathPlace} ({details.deceasedAge} ans)</p>}
                    </div>
                    <div className="border-t border-slate-200 pt-4">
                      <h4 className="text-xs font-black text-[#D22630] uppercase tracking-wider mb-2">Mode de livraison</h4>
                      <p className="text-sm font-bold text-slate-800 capitalize">{deliveryMethod === 'pdf' ? 'PDF Numérique Sécurisé' : deliveryMethod === 'guichet' ? 'Retrait au Guichet' : 'Colis Express Poste'}</p>
                    </div>
                    <div className="border-t border-slate-200 pt-4">
                      <div className="flex items-start gap-2 text-amber-700 bg-amber-50 rounded-xl p-3 text-xs">
                        <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
                        <span>En soumettant ce formulaire, vous certifiez sur l'honneur l'exactitude des informations fournies, conformément à la réglementation en vigueur sur l'état civil malagasy.</span>
                      </div>
                    </div>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>

            <div className="flex justify-between mt-8 pt-6 border-t border-slate-100">
              {step > 1 ? (
                <button type="button" onClick={handleBack} className="flex items-center gap-2 px-6 py-3 rounded-xl text-sm font-bold text-slate-600 hover:bg-slate-100 transition-all">
                  <ChevronLeft className="w-4 h-4" /> Précédent
                </button>
              ) : <div></div>}
              {step < 4 ? (
                <button type="button" onClick={handleNext} className="flex items-center gap-2 bg-[#D22630] hover:bg-red-700 text-white font-bold px-6 py-3 rounded-xl text-sm shadow-md transition-all">
                  Suivant <ChevronRight className="w-4 h-4" />
                </button>
              ) : (
                <button type="submit" disabled={submitting} className="flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-emerald-700 hover:from-emerald-700 hover:to-emerald-800 text-white font-bold px-8 py-3 rounded-xl text-sm shadow-md transition-all disabled:opacity-50">
                  {submitting ? (
                    <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span> Envoi...</>
                  ) : (
                    <><ShieldCheck className="w-4 h-4" /> Soumettre ma Demande</>
                  )}
                </button>
              )}
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
